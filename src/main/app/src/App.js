import React, {useState} from 'react';

import './App.css';
import Map from "./Map";
import InteractionPanel from "./InteractionPanel";
import SelectFileDialog from "./SelectFileDialog";
import SelectAlgorithmDialog from "./SelectAlgorithmDialog";
import Typography from "@material-ui/core/Typography";
import Console from 'react-component-console';
import Terminal from "./Terminal";
import SelectionProvider from "./SelectionContext";
import {SnackbarModel} from "./AlertModel";
import AlertProvider from "./AlertContext";
import MapProvider from "./MapContext";

function App() {
        const [toggleGraph, setToggleGraph]             = useState(false);
        const [toggleLandmarks, setToggleLandmarks]     = useState(false);
        const [toggleVisited, setToggleVisited]         = useState(false);
        const [selectedFile, setSelectedFile]           = useState(0);
        const [selectedAlgorithm, setSelectedAlgorithm]  = useState(0);
        const [lonSearch1, setLonSearch1]                 = useState();
        const [latSearch1, setLatSearch1]                 = useState();

        const [lonSearch2, setLonSearch2]                 = useState();
        const [latSearch2, setLatSearch2]                 = useState();

        const [latSearchResults, setLatSearchResults]   = useState([]);
        const [lonSearchResults, setLonSearchResults]   = useState([]);

        const [heuristicWeight, setHeuristicWeight]   = useState(1);

        const [elapsed, setElapsed] = useState(0);
        const [dist, setDist] = useState(0);
        const [hasPath, setHasPath] = useState(false);
        const [visits, setVisits] = useState(0);
        const [routeLength, setRouteLength] = useState(0);
        const [graphDetails, setGraphDetails] = useState({E:0, V:0});

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
            <AlertProvider>
                <SnackbarModel />
                <SelectionProvider>
                    <MapProvider>
                        <div className="App">
                            <div className="root" style={{display: 'flex'}}>
                                <div className="interaction-root" style={{
                                    width: '400px',
                                    position: 'absolute',
                                    zIndex: '10',
                                    background: 'transparent',
                                    margin: '20px',
                                    boxShadow: '0px 0px 4px 1px rgba(0,0,0,0.2), 0px 1px 1px 0px rgba(0,0,0,0.14), 0px 1px 3px 0px rgba(0,0,0,0.12);'
                                }}>

                                    <Typography variant="h5" component="h2">
                                        Shortest Path Visualization
                                    </Typography>
                                    <Console lines={[
                                        'A bachelor project by Mikkel Helmersen'
                                    ]} blink={false}/>
                                    {1 > 0 ?
                                            <>
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

                                                heuristicWeight     = {heuristicWeight}
                                                setHeuristicWeight  = {setHeuristicWeight}

                                                elapsed          = {elapsed}
                                                dist             = {dist}
                                                hasPath          = {hasPath}
                                                visits           = {visits}
                                                routeLength      = {routeLength}

                                                graphDetails = {graphDetails}

                                                submit              = {handleSubmit}
                                            />
                                        </>
                                        :
                                    <Terminal />
                                }

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
                                        selectedAlgorithm = {selectedAlgorithm}
                                        lonSearch1        = {lonSearch1}
                                        latSearch1        = {latSearch1}

                                        lonSearch2        = {lonSearch2}
                                        latSearch2        = {latSearch2}

                                        setLonSearchResults         = {setLonSearchResults}
                                        setLatSearchResults         = {setLatSearchResults}
                                        query                       = {query}
                                        setQuery                    = {setQuery}

                                        heuristicWeight     = {heuristicWeight}
                                        setElapsed          = {setElapsed}
                                        setDist             = {setDist}
                                        setHasPath          = {setHasPath}


                                        setRouteLength = {setRouteLength}
                                        setVisits = {setVisits}
                                        setGraphDetails = {setGraphDetails}
                                    />

                                </div>

                            </div>
                        </div>
                    </MapProvider>
                </SelectionProvider>
            </AlertProvider>
        );
}

export default App;
