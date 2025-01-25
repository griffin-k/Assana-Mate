import React, { useState } from 'react'

import { poseInstructions } from '../../utils/data'

export default function Instructions({ currentPose }) {
  const [instructions] = useState(poseInstructions)

  return (
    <div className="w-full mt-8 mb-8 mr-8 ml-8 max-w-lg mx-auto p-6 bg-white shadow-lg rounded-lg space-y-6">
      <h2 className="text-2xl font-semibold text-center text-gray-700">Instructions for {currentPose}</h2>
      
      <ul className="list-disc pl-5 space-y-3">
        {instructions[currentPose] ? (
          instructions[currentPose].map((instruction, index) => (
            <li key={index} className="text-lg text-gray-600">{instruction}</li>
          ))
        ) : (
          <li className="text-lg text-gray-600">Select a pose to view instructions</li>
        )}
      </ul>
      
   
    </div>
  )
}
