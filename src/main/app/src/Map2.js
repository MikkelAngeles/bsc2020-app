import React, {Component} from 'react';
import ReactMapGL, {Layer, Source} from 'react-map-gl';
import {fromJS} from 'immutable';
const accessToken = "pk.eyJ1IjoibWlra2VsYW5nZWxlcyIsImEiOiJjazltanR4YWUwMHhqM25xbTNjYTBidzFoIn0.NZCPthWITf6K5AeN8Xwn1Q";

const geojson = {
    type: 'FeatureCollection',
    features: [
        {type: 'Feature', geometry: {type: 'Point', coordinates: [-122.4, 37.8]}}
    ]
};


export default class Map2 extends Component {

    state = {
        viewport: {
            width: 400,
            height: 400,
            latitude: 37.7577,
            longitude: -122.4376,
            zoom: 8
        }
    };



    render() {
        return (
            <ReactMapGL
                {...this.state.viewport}
                onViewportChange={(viewport) => this.setState({viewport})}
                mapboxApiAccessToken    = {accessToken}
            >

                <Source id="my-data" type="geojson" data={geojson}>
                    <Layer
                        id="point"
                        type="circle"
                        paint={{
                            'circle-radius': 10,
                            'circle-color': '#007cbf'
                        }} />
                </Source>

            </ReactMapGL>
        );
    }
}