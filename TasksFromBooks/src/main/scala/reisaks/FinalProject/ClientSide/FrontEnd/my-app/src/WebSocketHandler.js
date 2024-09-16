// WebSocketContext.js
import React, { createContext, useContext, useState, useEffect } from 'react';

const WebSocketContext = createContext();

export const WebSocketProvider = ({ children }) => {
    const [socket, setSocket] = useState(null);
    const [message, setMessage] = useState('');

    const initializeSocket = (url, onOpen, onClose) => {
        const ws = new WebSocket(url);

        ws.onmessage = (event) => {
            setMessage(event.data);
        };

        ws.onopen = () => {
            onOpen();  // Notify when connection is established
        };

        ws.onclose = () => {
            onClose();  // Notify when connection closes
        };

        setSocket(ws);

        return () => {
            ws.close();
        };
    };

    useEffect(() => {
        // Cleanup the WebSocket connection on unmount
        return () => {
            if (socket) {
                socket.close();
            }
        };
    }, [socket]);

    const sendMessage = (msg) => {
        if (socket && socket.readyState === WebSocket.OPEN) {
            socket.send(msg);
        }
    };

    return (
        <WebSocketContext.Provider value={{ socket, message, sendMessage, initializeSocket }}>
            {children}
        </WebSocketContext.Provider>
    );
};

export const useWebSocket = () => useContext(WebSocketContext);




