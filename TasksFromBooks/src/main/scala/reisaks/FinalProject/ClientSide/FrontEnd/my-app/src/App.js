import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { WebSocketProvider } from './WebSocketHandler';
import WelcomePage from './Welcome';
import LobbyPage from './LobbyPage';
import TablePage from './TablePage'
import EndPage from './End'

const App = () => {
    return (
        <WebSocketProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<WelcomePage />} />
                    <Route path="/lobby" element={<LobbyPage />} />
                    <Route path="/table" element={<TablePage />} />
                    <Route path="/end" element={<EndPage />} />
                </Routes>
            </Router>
        </WebSocketProvider>
    );
};

export default App;



