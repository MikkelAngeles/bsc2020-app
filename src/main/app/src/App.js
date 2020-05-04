import React, {useState} from 'react';

import './App.css';
import Map from "./Map";
import InteractionPanel from "./InteractionPanel";
import Typography from "@material-ui/core/Typography";
import Console from 'react-component-console';
import SelectionProvider from "./SelectionContext";
import {SnackbarModel} from "./AlertModel";
import AlertProvider from "./AlertContext";
import MapProvider from "./MapContext";
import DetailsDrawer from "./DetailsDrawer";
import Paper from "@material-ui/core/Paper";

function App() {

        return (
            <AlertProvider>
                <SnackbarModel />
                <SelectionProvider>
                    <MapProvider>
                        <div className="App">
                            <div className="root" style={{display: 'flex'}}>

                                <div className="interaction-root" style={{
                                    position: 'absolute',
                                    zIndex: '10',
                                    background: 'transparent',
                                    margin: '20px',
                                    boxShadow: '0px 0px 4px 1px rgba(0,0,0,0.2), 0px 1px 1px 0px rgba(0,0,0,0.14), 0px 1px 3px 0px rgba(0,0,0,0.12);'
                                }}>

                                    <InteractionPanel />
                                </div>

                                <div className="map-root" style={{
                                    position: 'absolute',
                                    width: '100%',
                                    height: '100%',
                                    background: 'white',
                                    overflow: 'hidden'
                                }}>
                                    <Map />
                                </div>



                            </div>

                            <DetailsDrawer />
                        </div>
                    </MapProvider>
                </SelectionProvider>
            </AlertProvider>
        );
}

export default App;
