import React from 'react'
import logo from '../../utils/images/logo.png';


import { Link } from 'react-router-dom'

export default function Home() {
    return (
        <div className="flex flex-col items-center justify-center min-h-screen ">
            <div className="flex flex-col items-center bg-white shadow-lg rounded-lg p-8">
                <h1 className="text-4xl font-semibold text-blue-500 mb-4">AsanaMate</h1>

              
                <img 
                 src={logo} alt="Logo"
                    className="w-36 h-36 object-cover rounded-full mb-4"  // Tailwind classes for styling
                />
            </div>

            <h1 className="text-2xl text-gray-700 text-white mt-6 mb-4">Perfect Your Posture with AsanaMate</h1>
            
            <div className="flex justify-center mt-6 space-x-4">
                <Link to='/start'>
                    <button
                        className="bg-green-500 text-white px-8 py-3 rounded-lg hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-green-400"
                    >
                        Let's Start
                    </button>
                </Link>
            </div>
        </div>
    )
}
