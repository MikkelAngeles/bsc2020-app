import React, {useState} from 'react';

import './App.css';
import Map from "./Map";
import InteractionPanel from "./InteractionPanel";
import SelectFileDialog from "./SelectFileDialog";
import SelectAlgorithmDialog from "./SelectAlgorithmDialog";
import Typography from "@material-ui/core/Typography";

function App() {
        const [toggleGraph, setToggleGraph]             = useState(true);
        const [toggleLandmarks, setToggleLandmarks]     = useState(true);
        const [toggleVisited, setToggleVisited]         = useState(true);
        const [selectedFile, setSelectedFile]           = useState(0);
        const [selectedAlgorithm, setSelectedAlgorithm]  = useState(0);
        const [lonSearch1, setLonSearch1]                 = useState();
        const [latSearch1, setLatSearch1]                 = useState();

        const [lonSearch2, setLonSearch2]                 = useState();
        const [latSearch2, setLatSearch2]                 = useState();

        const [latSearchResults, setLatSearchResults]   = useState([]);
        const [lonSearchResults, setLonSearchResults]   = useState([]);

        const [query, setQuery]   = useState({
            lon1: 0,
            lat1: 0,
            lon2: 0,
            lat2: 0
        });

        function handleSubmit() {
            setQuery({
                lon1: lonSearch1,
                lat1: latSearch1,
                lon2: lonSearch2,
                lat2: latSearch2
            })
        }

        return (
            <div className="App">
                <div className="root" style={{display: 'flex'}}>
                    <div className="interaction-root" style={{
                        width: '400px',
                        position: 'absolute',
                        zIndex: '10000',
                        background: 'white',
                        margin: '20px',
                        borderRadius: '10px',
                        padding: '20px',
                        boxShadow: '0px 5px 8px rgba(0, 0, 0, 0.3)'
                    }}>

                        <InteractionPanel
                            onGraphToggle       = {() => setToggleGraph(!toggleGraph)}
                            toggleGraph         = {toggleGraph}

                            onToggleLandmarks   = {() => setToggleLandmarks(!toggleLandmarks)}
                            toggleLandmarks     = {toggleLandmarks}

                            onToggleVisited     = {() => setToggleVisited(!toggleVisited)}
                            toggleVisited       = {toggleVisited}

                            onLonSearch1      = {setLonSearch1}
                            onLatSearch1      = {setLatSearch1}
                            lonSearch1        = {lonSearch1}
                            latSearch1        = {latSearch1}

                            onLonSearch2      = {setLonSearch2}
                            onLatSearch2      = {setLatSearch2}
                            lonSearch2        = {lonSearch2}
                            latSearch2        = {latSearch2}

                            latSearchResults    = {latSearchResults}
                            lonSearchResults    = {lonSearchResults}
                            submit            = {handleSubmit}
                        />

                        <div>
                            <Typography>
                                Current:
                            </Typography>
                            <SelectFileDialog  selected={selectedFile} onSelect={setSelectedFile} />

                            <Typography>
                                Current:
                            </Typography>
                            <SelectAlgorithmDialog  selected={selectedAlgorithm} onSelect={setSelectedAlgorithm} />
                        </div>

                    </div>
                    <div className="map-root" style={{
                        position: 'absolute',
                        width: '100%',
                        height: '100%',
                        background: 'white',
                        overflow: 'hidden'
                    }}>
                        <Map
                            toggleGraph      = {toggleGraph}
                            toggleLandmarks  = {toggleLandmarks}
                            toggleVisited    = {toggleVisited}
                            selectedFile     = {selectedFile}

                            lonSearch1        = {lonSearch1}
                            latSearch1        = {latSearch1}

                            lonSearch2        = {lonSearch2}
                            latSearch2        = {latSearch2}

                            setLonSearchResults        = {setLonSearchResults}
                            setLatSearchResults        = {setLatSearchResults}
                            query                   = {query}
                            setQuery                = {setQuery}
                        />

                    </div>

                </div>
            </div>
        );
}

export default App;
