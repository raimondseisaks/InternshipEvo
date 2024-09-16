import React, { useState, useEffect } from 'react';
import { useWebSocket } from './WebSocketHandler';
import './lobbyStyle.css';
import {useNavigate} from "react-router-dom";

const LobbyPage = () => {
    const navigate = useNavigate();
    const [availableTables, setAvailableTables] = useState([]);
    const { socket, message, sendMessage } = useWebSocket(); // Use WebSocket from context

    useEffect(() => {
        if (socket) {
            socket.onmessage = (event) => {
                if (event.data.match(/Table-(\d+) has (\d+) seats/)) {
                    updateTableList(event.data);
                }
            };
        }
    }, [socket]);

    const updateTableList = (message) => {
        const matches = message.match(/Table-(\d+) has (\d+) seats/);
        if (matches) {
            const tableNumber = matches[1];
            const seats = matches[2];

            setAvailableTables(prevTables => {
                const existingIndex = prevTables.findIndex(item => item.tableNumber === tableNumber);
                if (existingIndex > -1) {
                    const newTables = [...prevTables];
                    newTables[existingIndex].seats = seats;
                    return newTables;
                }
                return [...prevTables, { tableNumber, seats }];
            });
        }
    };

    const handleJoinTable = (tableNumber) => {
        const command = `Join-Table Table-${tableNumber}`;
        sendMessage(command);
        navigate('/table')

    };

    const handleShowAvailability = () => {
        const command = "Show-Available-Tables";
        sendMessage(command);
    };

    const handleExitServer = () => {
        const command = "Exit-Server";
        sendMessage(command);
        navigate('/End')
    };

    return (
        <div>
            <nav className="navbar bg-danger">
                <div className="container-fluid">
                    <a className="navbar-brand text-light text" href="#">Spinning Wheel Server</a>
                    <button id="exit-server" className="btn btn-outline-light" onClick={handleExitServer}>Exit-Server
                    </button>
                </div>
            </nav>

            <div className="allTables">
                <h1 className="text">WELCOME TO THE LOBBY - CHOOSE TABLE</h1>
                <div className="text-center">
                    <div className="row mt-5">
                        <div className="col">
                            <img className="tableImg" src={`${process.env.PUBLIC_URL}/Assets/table1.png`} alt="table1"/>
                            <button id="join-table-1" type="submit" className="btn btn-outline-danger mt-2" onClick={() => handleJoinTable(1)}>Join to
                                Table 1
                            </button>
                        </div>
                        <div className="col">
                            <img className="tableImg" src={`${process.env.PUBLIC_URL}/Assets/table2.png`} alt="table1"/>

                            <button id="join-table-2" type="submit" className="btn btn-outline-danger mt-2" onClick={() => handleJoinTable(2)}>Join to
                                Table 2
                            </button>
                        </div>
                        <div className="col">
                            <img className="tableImg" src={`${process.env.PUBLIC_URL}/Assets/table3.png`} alt="table1"/>

                            <button id="join-table-3" type="submit" className="btn btn-outline-danger mt-2 " onClick={() => handleJoinTable(3)}>Join to
                                Table 3
                            </button>
                        </div>
                    </div>

                    <div className="row mt-5">
                        <div className="col">
                            <img className="tableImg" src={`${process.env.PUBLIC_URL}/Assets/table4.png`} alt="table1"/>

                            <button id="join-table-4" type="submit" className="btn btn-outline-danger mt-2" onClick={() => handleJoinTable(4)}>Join to
                                Table 4
                            </button>
                        </div>
                        <div className="col">
                            <img className="tableImg" src={`${process.env.PUBLIC_URL}/Assets/table5.png`} alt="table1"/>

                            <button id="join-table-5" type="submit" className="btn btn-outline-danger mt-2" onClick={() => handleJoinTable(5)}>Join to
                                Table 5
                            </button>
                        </div>
                        <div className="col">
                            <img className="tableImg" src={`${process.env.PUBLIC_URL}/Assets/table6.png`} alt="table1"/>
                            <button id="join-table-6" type="submit" className="btn btn-outline-danger mt-2" onClick={() => handleJoinTable(6)}>Join to
                                Table 6
                            </button>
                        </div>
                    </div>

                    <div className="row mt-5">
                        <div className="col">
                            <img className="tableImg" src={`${process.env.PUBLIC_URL}/Assets/table7.png`} alt="table1"/>

                            <button id="join-table-7" type="submit" className="btn btn-outline-danger mt-2" onClick={() => handleJoinTable(7)}>Join to
                                Table 7
                            </button>
                        </div>
                        <div className="col">
                            <img className="tableImg" src={`${process.env.PUBLIC_URL}/Assets/table8.png`} alt="table1"/>

                            <button type="submit" className="btn btn-outline-danger mt-2" onClick={() => handleJoinTable(8)}>Join to Table 8</button>
                        </div>
                        <div className="col">
                            <img className="tableImg" src={`${process.env.PUBLIC_URL}/Assets/table9.png`} alt="table1"/>

                            <button type="submit" className="btn btn-outline-danger mt-2" onClick={() => handleJoinTable(9)}>Join to Table 9</button>
                        </div>
                    </div>
                </div>
            </div>

            <div className="gameRules">
                <h1 id="rulesHeader">Game rules</h1>
                <p id="rulesText"> Spinning wheel contains 100 different sectors, numbered from 1 to 100. During each
                    round, wheel is being rotated for 10 seconds and it stops randomly at some sector.
                    Odd sectors have a winning multiplier equal to : bet x 2
                    Even sectors (except for sector number 100) has a winning multiplier : bet x 3
                    Sector number 100 has a winning multiplier : bet x 50

                    Game rounds happen one after another, using automatic scheduling.</p>
            </div>
            <div className="availability">
                <button id="show-button" className="btn btn-outline-danger btn-lg"
                        onClick={handleShowAvailability}>Show-Available-Tables
                </button>
                <ul id="table-list">
                    {availableTables.map(table => (
                        <li key={table.tableNumber}>Table-{table.tableNumber} has {table.seats} seats</li>
                    ))}
                </ul>
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

export default LobbyPage;
