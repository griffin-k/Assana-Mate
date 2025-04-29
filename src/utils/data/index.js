export const poseInstructions = {
    Tree: [
        'Start in Mountain Pose with both feet grounded.',
        'Bend one leg and place the foot on the inner thigh of the standing leg.',
        'Clasp your hands in Anjali Mudra and lengthen your body.',
        'Hold and breathe, then switch legs after a while.'
    ],
    Cobra: [
        'Lie prone, with hands under shoulders and legs extended back.',
        'Straighten arms to lift the chest, maintaining connection to your legs.',
        'Firm the shoulder blades, lift through the sternum, and distribute the backbend evenly.',
        'Hold for 15-30 seconds, then lower down.'
    ],
    Dog: [
        'Start on hands and knees, with palms spread and toes tucked.',
        'Lift knees and hips, straightening legs towards the floor.',
        'Firm arms, press index fingers into the floor, and relax your head between arms.',
        'Hold for 10 breaths, then return to Child’s Pose.'
    ],
    Chair: [
        'Stand tall with feet wider than hip-width apart.',
        'Lift arms overhead and bend knees, creating a right angle with thighs.',
        'Hold for 30 seconds to 1 minute.'
    ],
    Warrior: [
        'Start in a lunge with arms above head, hips squared.',
        'Move hands to prayer position, lean forward with back leg extended.',
        'Straighten the standing leg and form a T-shape with your arms.'
    ],
    Triangle: [
        'Stand with feet wide, turn left foot out and raise arms.',
        'Straighten left leg and reach torso over, placing left hand on the mat.',
        'Gaze toward the extended right arm and repeat on the other side.'
    ],
    Shoulderstand: [
        'Start lying on folded blankets and walk shoulders under your back.',
        'Lift hips and legs, keeping your neck straight and gaze upward.',
        'Ensure hips are over shoulders and hold for up to 10 breaths.'
    ]
}



export const tutorials = [
    '1. When App ask for permission of camera, allow it to access to capture pose.',
    '2. Select what pose you want to do in the dropdown.',
    '3. Read Instrctions of that pose so you will know how to do that pose.',
    '4. Click on Start pose and see the image of the that pose in the right side and replecate that image in front of camera.',
    '5. If you will do correctly the skeleton over the video will become green in color and sound will start playing'
]

export const fixCamera = [
    'Solution 1. Make sure you have allowed the permission of camera, if you have denined the permission, go to setting of your browser to allow the access of camera to the application.',
    'Solution 2. Make sure no any other application is not accessing camera at that time, if yes, close that application',
    'Solution 3. Try to close all the other opened broswers'
] 

export const POINTS = {
    NOSE : 0,
    LEFT_EYE : 1,
    RIGHT_EYE : 2,
    LEFT_EAR : 3,
    RIGHT_EAR : 4,
    LEFT_SHOULDER : 5,
    RIGHT_SHOULDER : 6,
    LEFT_ELBOW : 7,
    RIGHT_ELBOW : 8,
    LEFT_WRIST : 9,
    RIGHT_WRIST : 10,
    LEFT_HIP : 11,
    RIGHT_HIP : 12,
    LEFT_KNEE : 13,
    RIGHT_KNEE : 14,
    LEFT_ANKLE : 15,
    RIGHT_ANKLE : 16,
}

export const keypointConnections = {
    nose: ['left_ear', 'right_ear'],
    left_ear: ['left_shoulder'],
    right_ear: ['right_shoulder'],
    left_shoulder: ['right_shoulder', 'left_elbow', 'left_hip'],
    right_shoulder: ['right_elbow', 'right_hip'],
    left_elbow: ['left_wrist'],
    right_elbow: ['right_wrist'],
    left_hip: ['left_knee', 'right_hip'],
    right_hip: ['right_knee'],
    left_knee: ['left_ankle'],
    right_knee: ['right_ankle']
}