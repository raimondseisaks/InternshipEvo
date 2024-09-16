// WelcomePage.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useWebSocket } from './WebSocketHandler';
import './welcomeStyle.css';

const WelcomePage = () => {
    const [playerId, setPlayerId] = useState('');
    const {initializeSocket, socket } = useWebSocket();
    const navigate = useNavigate();

    const handleSubmit = (event) => {
        event.preventDefault();

        if (!playerId) {
            alert('Please provide Player ID.');
            return;
        }

        initializeSocket(
            `ws://localhost:8080/${encodeURIComponent(playerId.replace(/ /g, ''))}`,
            () => {
                console.log('WebSocket connection established');
                navigate('/lobby');
            },
            () => {
                alert('Failed to establish connection. Choose a unique ID.');
            }
        );


    };


    return (
        <div>
            <div id="mainview"></div>
            <div id="login">
                <h1>Join the gaming service</h1>
                <form id="welcomeForm" onSubmit={handleSubmit}>
                    <input
                        type="text"
                        id="playerId"
                        placeholder="Unique Player ID"
                        value={playerId}
                        onChange={(e) => setPlayerId(e.target.value)}
                        required
                    />
                    <button type="submit" className="btn btn-outline-danger">Connect</button>
                </form>
            </div>
            <div id="footer">
                <h2>Evolution Internship Final Project</h2>
                <div className="footer-contact">
                    <p>Contact Me: <a href="mailto:eisaks83@gmail.com" className={"link-light link-offset-2 link-underline-opacity-25 link-underline-opacity-100-hover"}>eisaks83@gmail.com</a></p>
                    <p>Author: Raimonds Eisaks</p>
                </div>
            </div>

        </div>
    );
};

export default WelcomePage;


