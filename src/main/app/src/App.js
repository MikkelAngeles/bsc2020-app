import React from 'react';

import './App.css';
import Map from "./Map";
import InteractionPanel from "./InteractionPanel";
import Map2 from "./Map2";

function App() {
  return (
    <div className="App">
        <div className="root" style={{display: 'flex'}}>
            <div className="map-root" >
                <div>Map panel</div>
                <Map />
            </div>
            <div className="interaction-root" style={{width: '100%'}}>
                <InteractionPanel />
            </div>
        </div>

    </div>
  );
}

export default App;
