import * as poseDetection from '@tensorflow-models/pose-detection';
import * as tf from '@tensorflow/tfjs';
import React, { useRef, useState, useEffect } from 'react';
import backend from '@tensorflow/tfjs-backend-webgl';
import Webcam from 'react-webcam';
import { count } from '../../utils/music'; 

import Instructions from '../../components/Instrctions/Instructions';
import './Yoga.css';
import DropDown from '../../components/DropDown/DropDown';
import { poseImages } from '../../utils/pose_images';
import { POINTS, keypointConnections } from '../../utils/data';
import { drawPoint, drawSegment } from '../../utils/helper';

let skeletonColor = 'rgb(255,255,255)';
let poseList = ['Tree', 'Chair', 'Cobra', 'Warrior', 'Dog', 'Shoulderstand', 'Traingle'];
let interval;
let flag = false;

function Yoga() {
  const webcamRef = useRef(null);
  const canvasRef = useRef(null);

  const [startingTime, setStartingTime] = useState(0);
  const [currentTime, setCurrentTime] = useState(0);
  const [poseTime, setPoseTime] = useState(0);
  const [bestPerform, setBestPerform] = useState(0);
  const [currentPose, setCurrentPose] = useState('Tree');
  const [isStartPose, setIsStartPose] = useState(false);
  const [nextPoseCountdown, setNextPoseCountdown] = useState(null);
  const [poseFeedback, setPoseFeedback] = useState("❌ Waiting for correct pose");

  const CLASS_NO = {
    Chair: 0,
    Cobra: 1,
    Dog: 2,
    No_Pose: 3,
    Shoulderstand: 4,
    Traingle: 5,
    Tree: 6,
    Warrior: 7,
  };

  function get_center_point(landmarks, left_bodypart, right_bodypart) {
    let left = tf.gather(landmarks, left_bodypart, 1);
    let right = tf.gather(landmarks, right_bodypart, 1);
    return tf.add(tf.mul(left, 0.5), tf.mul(right, 0.5));
  }

  function get_pose_size(landmarks, torso_size_multiplier = 2.5) {
    let hips_center = get_center_point(landmarks, POINTS.LEFT_HIP, POINTS.RIGHT_HIP);
    let shoulders_center = get_center_point(landmarks, POINTS.LEFT_SHOULDER, POINTS.RIGHT_SHOULDER);
    let torso_size = tf.norm(tf.sub(shoulders_center, hips_center));
    let pose_center = get_center_point(landmarks, POINTS.LEFT_HIP, POINTS.RIGHT_HIP);
    pose_center = tf.expandDims(pose_center, 1);
    pose_center = tf.broadcastTo(pose_center, [1, 17, 2]);
    let d = tf.gather(tf.sub(landmarks, pose_center), 0, 0);
    let max_dist = tf.max(tf.norm(d, 'euclidean', 0));
    return tf.maximum(tf.mul(torso_size, torso_size_multiplier), max_dist);
  }

  function normalize_pose_landmarks(landmarks) {
    let pose_center = get_center_point(landmarks, POINTS.LEFT_HIP, POINTS.RIGHT_HIP);
    pose_center = tf.expandDims(pose_center, 1);
    pose_center = tf.broadcastTo(pose_center, [1, 17, 2]);
    landmarks = tf.sub(landmarks, pose_center);
    let pose_size = get_pose_size(landmarks);
    return tf.div(landmarks, pose_size);
  }

  function landmarks_to_embedding(landmarks) {
    landmarks = normalize_pose_landmarks(tf.expandDims(landmarks, 0));
    return tf.reshape(landmarks, [1, 34]);
  }

  const runMovenet = async () => {
    const detectorConfig = { modelType: poseDetection.movenet.modelType.SINGLEPOSE_THUNDER };
    const detector = await poseDetection.createDetector(poseDetection.SupportedModels.MoveNet, detectorConfig);
    const poseClassifier = await tf.loadLayersModel('https://models.s3.jp-tok.cloud-object-storage.appdomain.cloud/model.json');
    const countAudio = new Audio(count);
    countAudio.loop = true;

    interval = setInterval(() => {
      detectPose(detector, poseClassifier, countAudio);
    }, 30); // faster tracking
  };

  const detectPose = async (detector, poseClassifier, countAudio) => {
    if (
      webcamRef.current?.video?.readyState === 4
    ) {
      const video = webcamRef.current.video;
      const pose = await detector.estimatePoses(video);
      const ctx = canvasRef.current.getContext('2d');
      ctx.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height);

      try {
        const keypoints = pose[0]?.keypoints || [];
        let notDetected = 0;

        let input = keypoints.map((keypoint) => {
          if (keypoint.score > 0.4) {
            if (!(keypoint.name === 'left_eye' || keypoint.name === 'right_eye')) {
              drawPoint(ctx, keypoint.x, keypoint.y, 8, 'white');
              let connections = keypointConnections[keypoint.name];
              connections?.forEach((conn) => {
                drawSegment(ctx, [keypoint.x, keypoint.y], [keypoints[POINTS[conn.toUpperCase()]]?.x || 0, keypoints[POINTS[conn.toUpperCase()]]?.y || 0], skeletonColor);
              });
            }
          } else {
            notDetected += 1;
          }
          return [keypoint.x, keypoint.y];
        });

        if (notDetected > 4) {
          skeletonColor = 'white';
          setPoseFeedback("❌ Pose not detected");
          return;
        }

        const processedInput = landmarks_to_embedding(input);
        const classification = poseClassifier.predict(processedInput);

        classification.array().then((data) => {
          const classNo = CLASS_NO[currentPose];
          if (data[0][classNo] > 0.97) {
            if (!flag) {
              countAudio.play();
              setStartingTime(Date.now());
              flag = true;
            }
            setCurrentTime(Date.now());
            skeletonColor = 'lime';
            setPoseFeedback("✅ Correct Pose");
          } else {
            flag = false;
            setPoseFeedback("❌ Wrong Pose");
            skeletonColor = 'white';
            countAudio.pause();
            countAudio.currentTime = 0;
          }
        });
      } catch (err) {
        console.log(err);
      }
    }
  };

  useEffect(() => {
    if (flag) {
      const timeDiff = (currentTime - startingTime) / 1000;
      setPoseTime(timeDiff);
      if (timeDiff > bestPerform) setBestPerform(timeDiff);

      if (timeDiff >= 60 && nextPoseCountdown === null) {
        let countdown = 3;
        setNextPoseCountdown(countdown);
        const countdownInterval = setInterval(() => {
          countdown -= 1;
          setNextPoseCountdown(countdown);
          if (countdown === 0) {
            clearInterval(countdownInterval);
            goToNextPose();
          }
        }, 1000);
      }
    }
  }, [currentTime]);

  const goToNextPose = () => {
    const currentIndex = poseList.indexOf(currentPose);
    const nextIndex = (currentIndex + 1) % poseList.length;
    setCurrentPose(poseList[nextIndex]);
    setStartingTime(0);
    setCurrentTime(0);
    setPoseTime(0);
    setBestPerform(0);
    setNextPoseCountdown(null);
    flag = false;
  };

  useEffect(() => {
    setCurrentTime(0);
    setPoseTime(0);
    setBestPerform(0);
  }, [currentPose]);

  function startYoga() {
    setIsStartPose(true);
    runMovenet();
  }

  function stopPose() {
    setIsStartPose(false);
    clearInterval(interval);
  }

  if (isStartPose) {
    return (
      <div className="yoga-main-container">
        <div className="left-panel">
          <div className="card pose-card">
            <h3>Pose Image</h3>
            <img src={poseImages[currentPose]} className="pose-img" alt="pose" />
          </div>

          <div className="card feedback-card">
            <h3>Feedback</h3>
            <div className="feedback">{poseFeedback}</div>
            <div className="performance">
              <p>Pose Time: {poseTime.toFixed(1)}s</p>
              <p>Best: {bestPerform.toFixed(1)}s</p>
              {nextPoseCountdown !== null && <p>Next pose in: {nextPoseCountdown}s</p>}
            </div>
            <button onClick={stopPose} className="secondary-btn">Stop</button>
          </div>
        </div>

        <div className="right-panel">
          <div className="card webcam-card">
            <h3>Live Feed</h3>
            <div className="video-container">
              <Webcam
                width="640"
                height="480"
                ref={webcamRef}
                id="webcam"
              />
              <canvas
                ref={canvasRef}
                width="640"
                height="480"
                id="my-canvas"
              />
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="yoga-container">
      <DropDown poseList={poseList} currentPose={currentPose} setCurrentPose={setCurrentPose} />
      <Instructions currentPose={currentPose} />
      <button onClick={startYoga} className="secondary-btn">Start Pose</button>
    </div>
  );
}

export default Yoga;
