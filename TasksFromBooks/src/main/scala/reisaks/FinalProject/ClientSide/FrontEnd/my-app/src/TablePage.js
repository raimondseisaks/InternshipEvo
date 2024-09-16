import React, {useEffect, useRef, useState} from 'react';
import {useWebSocket} from './WebSocketHandler';
import './tableStyle.css';
import {useNavigate} from "react-router-dom";

const TablePage = () => {
    const { socket, sendMessage } = useWebSocket();
    const [betCode, setBetCode] = useState('');
    const [amount, setAmount] = useState('');
    const wheelRef = useRef(null);
    const navigate = useNavigate();

    let stateMessages = ["Betting has ended!", "Game is started! Wheel is spinning.......", "Round has ended"]
    let bets = [];

    useEffect(() => {
        if (socket) {
            socket.onmessage = (event) => {
                const message = event.data;


                if (!isNaN(message)) {
                    spinWheel(message)
                }
                else if (stateMessages.includes(message)) {
                    let logText = document.getElementById("tableState")
                    logText.textContent = "Wheel state: " + message
                }
                else if (message === "Round started. Place your bets!") {
                    let logText = document.getElementById("tableState")
                    logText.textContent = "Wheel state: " + message
                    bets = []
                    updateBetLog();
                }
                else if (message.startsWith("You bet on")) {
                    const betRegex = /You bet on (\w+) with amount (\d+)/;
                    const match = message.match(betRegex);

                    if (match) {
                        const betCode = match[1];
                        const amount = match[2];

                        bets.push({ betCode, amount });

                        updateBetLog();
                    }
                }
                else  {
                    let logText = document.getElementById("roundResult");
                    logText.textContent = "Message from server: " + message;
                }
            };
        }
    }, [socket]);

    const updateBetLog = (defaultMessage = "") => {
        const logText = document.getElementById("bets");

        if (bets.length === 0) {
            logText.innerHTML = "No bets yet.";
        } else {
            logText.innerHTML = bets.map(bet => `Bet Code: ${bet.betCode}, Amount: ${bet.amount}`).join("<br>");
        }
    };


    function spinWheel(roll) {
        const wheel = document.querySelector(".roulette-wrappers .wheels");

        const order = Array.from({ length: 100 }, (_, i) => i + 1);
        const position = order.indexOf(Number(roll) + 1);

        const rows = 11;
        const card = 75 + 3 * 2;


        let landingPosition = (rows * 100 * card + position * card + 30) - (card*51);
        const randomize = Math.floor(Math.random() * 75) - (75 / 3);
        landingPosition += randomize;

        const object = {
            x: (Math.floor(Math.random() * 50) / 100).toFixed(2),
            y: (Math.floor(Math.random() * 20) / 100).toFixed(2),
        };


        wheel.style.transitionTimingFunction = `cubic-bezier(0, ${object.x}, ${object.y}, 1)`;
        wheel.style.transitionDuration = '5s';
        wheel.style.transform = `translate3d(-${landingPosition}px, 0px, 0px)`;

        setTimeout(() => {
            wheel.style.transitionTimingFunction = '';
            wheel.style.transitionDuration = '';

            const resetTo = -(position * card + randomize + 30 - (card*51));
            wheel.style.transform = `translate3d(${resetTo}px, 0px, 0px)`;
        }, 5 * 1000);
    }

    const handleBetChange = (e) => {
        setBetCode(e.target.value);
    };
    const handleAmountChange = (v) => {
        setAmount(v.target.value);
    };

    const handleExitTable = () => {
        const command = "Exit-Table";
        sendMessage(command);
        navigate('/lobby')
    };

    const handleSubmitBet = (e) => {
        e.preventDefault()
        if (betCode && amount) {
            const command = `Add-Bet ${betCode} ${amount}`;
            sendMessage(command);
            setAmount("")
            setBetCode("")
        }
    };

    useEffect(() => {
        if (wheelRef.current) {
            initWheel();
        }
    }, []);

    const initWheel = () => {
        if (wheelRef.current) {
            const $wheel = wheelRef.current;
            let rows = "";

            let row = "";
            for (let i = 1; i <= 99; i++) {
                const color = i % 2 === 0 ? 'blacks' : 'reds';
                row += `<div class='cards ${color}'>${i}</div>`;
            }
            row += "<div class='cards greens'>100</div>";
            row = `<div class='rows'>${row}</div>`;

            rows = new Array(201).fill(row).join("");

            $wheel.innerHTML = rows;
        }
    };




    return (
        <div>
            <nav className="navbar bg-danger">
                <div className="container-fluid">
                    <a className="navbar-brand text-light text" href="#">Spinning Wheel Server</a>
                    <button id="exit-table" className="btn btn-outline-light" onClick={handleExitTable}>Exit-Table</button>
                </div>
            </nav>
            <div className="roulettes">
                <div className="roulette-wrappers">
                    <div className="selectors"></div>
                    <div className="wheels" ref={wheelRef}></div>
                </div>
            </div>
            <div className="container">
                <div className="bettingSys">
                    <h1>Add Bet</h1>
                    <div className="form-group">
                        <label htmlFor="betCode">Choose sector :</label>
                        <input
                            type="text"
                            id="betCode"
                            placeholder="Enter bet code"
                            value={betCode}
                            onChange={handleBetChange}
                        />
                        <label htmlFor="betCode">Write bet:</label>
                        <input
                            type="text"
                            id="amount"
                            placeholder="Enter Amount"
                            value={amount}
                            onChange={handleAmountChange}
                        />
                        <button type="submit" className="btn btn-outline-danger" onClick={handleSubmitBet}>Submit Bet
                        </button>
                    </div>
                </div>
                <div className="announcment">
                    <h2>State of Spinning Wheel</h2>
                    <p id="tableState"></p>
                    <p id="bets"></p>
                    <p id="roundResult"></p>
                </div>
            </div>
            <div id="footer">
                <h2>Evolution Internship Final Project</h2>
                <div className="footer-contact">
                    <p>Contact Me: <a href="mailto:eisaks83@gmail.com"
                                      className={"link-light link-offset-2 link-underline-opacity-25 link-underline-opacity-100-hover"}>eisaks83@gmail.com</a>
                    </p>
                    <p>Author: Raimonds Eisaks</p>
                </div>
            </div>
        </div>
    );
};

export default TablePage;
