import React from 'react';
import './endStyle.css'

const Welcome = () => {
    return (
        <div>
            <nav className="navbar bg-danger">
                <div className="container-fluid">
                    <a className="navbar-brand text-light text" href="#">Spinning Wheel Server</a>
                </div>
            </nav>
            <div className="thanks">
                <h1>Thanks for gaming</h1>
                <a className="btn btn-outline-danger" href="http://localhost:3000/">Login Back!</a>
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

export default Welcome;
