import React from 'react';
import Button from "@material-ui/core/Button";
import SearchPanel from "./SearchPanel";
import InputBase from "@material-ui/core/InputBase";
import TextField from "@material-ui/core/TextField";

export default function InteractionPanel () {
    return (
        <div>
            Interaction Panel

            <SearchPanel />

            <div>
                <div>
                    <div>Route</div>
                    <TextField id="standard-basic" label="longitude" />
                    <TextField id="standard-basic" label="latitude" />

                    <TextField id="standard-basic" label="longitude" />
                    <TextField id="standard-basic" label="latitude" />

                    <Button variant="outlined" color="primary">
                        Calculate
                    </Button>
                </div>
                <div>Route details</div>
                <p>From: not selected</p>
                <p>To: not selected</p>
                <Button variant="outlined" color="primary">
                    Toggle route
                </Button>
                <Button variant="outlined" color="primary">
                    Toggle visited vertices
                </Button>
            </div>

            <Button variant="outlined" color="primary">
                Center
            </Button>

            <div>
                <div>Graph details</div>
                <p>Vertices: 0</p>
                <p>Edges: 0</p>
                <p>Landmarks: 0</p>
            </div>

            <Button variant="outlined" color="primary">
                Toggle Landmarks
            </Button>

            <Button variant="outlined" color="primary">
                Toggle graph region
            </Button>
            <Button variant="outlined" color="primary">
                Clear all selections
            </Button>


            <div>
                <div>Draw</div>
                <TextField id="standard-basic" label="longitude" />
                <TextField id="standard-basic" label="latitude" />
                <Button variant="outlined" color="primary">
                    Draw
                </Button>
                <Button variant="outlined" color="primary">
                    Move to
                </Button>
            </div>

            <div>
                <div>Draw line</div>

                <TextField id="standard-basic" label="longitude" />
                <TextField id="standard-basic" label="latitude" />

                <TextField id="standard-basic" label="longitude" />
                <TextField id="standard-basic" label="latitude" />

                <Button variant="outlined" color="primary">
                    Draw
                </Button>
                <Button variant="outlined" color="primary">
                    Move to
                </Button>
            </div>

        </div>
    );
}