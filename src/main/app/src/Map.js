import React, {useEffect, useRef, useState} from 'react';
import MapGL, {Layer, Source} from 'react-map-gl';
import axios from "axios";
import LinearProgress from "@material-ui/core/LinearProgress";

const accessToken = "pk.eyJ1IjoibWlra2VsYW5nZWxlcyIsImEiOiJjazltanR4YWUwMHhqM25xbTNjYTBidzFoIn0.NZCPthWITf6K5AeN8Xwn1Q";

const db = [
    [10.579150390623285,57.72461531315225],
    [12.282031249997033,56.09954580268264],
    [10.732958984373925,54.68328220960388],
    [8.129199218748925,55.60620642188443],
    [11.534960937498278,55.36967133830027],
    [9.370654296873628,56.22190373231677]
];

export default function Map (props) {
    const {toggleGraph = true, toggleLandmarks, toggleVisited, selectedFile} = props;
    const {lonSearch, latSearch, setLonSearchResults, setLatSearchResults} = props;
    const [viewport, setViewport] = useState({
            width: window.outerWidth,
            height: window.outerHeight + 50,
            latitude: 55.6,
            longitude:  10.7,
            zoom: 6
    });

    const [source, setSource] = useState(-1);
    const [target, setTarget] = useState(-1);

    const [nearest, setNearest] = useState(buildPoint([]));
    const [routeFrom, setRouteFrom] = useState(buildPoint([]));
    const [routeTo, setRouteTo] = useState(buildPoint([]));
    const [route, setRoute] = useState(buildPoint([]));

    const [landmarks, setLandmarks] = useState(buildLineString(db));

    const [graph, setGraph] = useState(buildLineString([]));

    const [visited, setVisited] = useState(buildLineString([]));

    const [graphLoading, setGraphLoading] = useState(false);

    function handleChange(vp) { setViewport(vp); }

    function loadFile() {
        setGraphLoading(true);
        let path = 'http://localhost:8080/load';
        if(selectedFile === 0) {
            path += '/json'
        } else if(selectedFile === 1) {
            path += '/dimacs/nyc'
        }
        axios.get(path)
            .then(function (response) {
                // handle success
                if(response.status === 200) handleLoad();
                console.log(response);
            })
            .catch(function (error) {
                // handle error
                console.log(error);
            })
            .finally(function () {
                setGraphLoading(false);
            });
    }

    function handleLoad() {
        setGraphLoading(true);
        axios.get('http://localhost:8080//vertices/trimmed')
            .then(function (response) {
                // handle success
                if(response.data) {
                    setGraph(buildLineString(response.data));
                }
                console.log(response);
            })
            .catch(function (error) {
                // handle error
                console.log(error);
            })
            .finally(function () {
                setGraphLoading(false);
            });
    }

    function getRoute() {
        if(source === -1|| target === -1) return;
        setGraphLoading(true);
        axios.get(`http://localhost:8080/route?from=${source}&to=${target}`)
            .then(function (response) {
                // handle success
                if(response.data) {
                    setRoute(buildLineString(response.data));
                    getVisited();
                }
                console.log(response);
            })
            .catch(function (error) {
                setGraphLoading(false);
                console.log(error);
            })
    }

    function getVisited() {
        axios.get(`http://localhost:8080/route/visited`)
            .then(function (response) {
                // handle success
                if(response.data) {
                    setVisited(buildLineString(response.data));
                }
                console.log(response);
            })
            .catch(function (error) {
                // handle error
                console.log(error);
            })
            .finally(function () {
                setGraphLoading(false);
            });
    }

    function clear() {
        let point = buildPoint([]);
        setRoute(buildLineString([]));
        setVisited(buildLineString([]));
        setRouteFrom(point);
        setRouteTo(point);
        setSource(-1);
        setTarget(-1)
    }

    function getGraph() {
        return graph.features[0].geometry.coordinates;
    }

    function handleOnClick(e) {
        //console.log(e);
        let cur = e.lngLat;
        let cur_lng = cur[0];
        let cur_lat = cur[1];
        let G = getGraph();

        if(e && e.rightButton) {

            if (routeFrom.features[0].geometry.coordinates.length === 0) {
                let rs =  findNearest(cur_lng, cur_lat, G);
                if(rs > -1) {
                    setRouteFrom(buildPoint(G[rs]));
                    setSource(rs);
                }
            }
            else if (routeTo.features[0].geometry.coordinates.length === 0) {
                let rs =  findNearest(cur_lng, cur_lat, G);
                if(rs > -1) {
                    setRouteTo(buildPoint(G[rs]));
                    setTarget(rs);
                }
            } else {
                clear();
            }
        } else if(e.leftButton) {
            let rs =  findNearest(cur_lng, cur_lat, G);
            if(rs > -1) {
                setNearest(buildPoint(G[rs]));
            }
        }
    }

    function findNearest(cur_lng, cur_lat, arr) {
        let radius = 10 / viewport.zoom;
        let result = Infinity;
        let nearest = -1;
        for (var i=0; i < arr.length;i++){
            var ans = haversine(cur_lat, arr[i][1], cur_lng, arr[i][0]);
            if (ans < result){//nearest
                result = ans;
                nearest = i;
            }
        }
        if(nearest !== -1) {
            console.log("Nearest id " + i + " distance: " + result + " coordinates: [" + arr[nearest] + "]");
            //setNearest(buildPoint(arr[nearest]));
        }
        return nearest;
    }

    function toRad(Value) {
        /** Converts numeric degrees to radians */
        return Value * Math.PI / 180;
    }

    function haversine(lat1,lat2,lng1,lng2){
        let rad = 6372.8; // for km Use 3961 for miles
        let deltaLat = toRad(lat2-lat1);
        let deltaLng = toRad(lng2-lng1);
        lat1 = toRad(lat1);
        lat2 = toRad(lat2);
        let a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) + Math.sin(deltaLng/2) * Math.sin(deltaLng/2) * Math.cos(lat1) * Math.cos(lat2);
        let c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return  rad * c;
    }

    function buildPoint(pt) {
        return ({
            type: 'FeatureCollection',
            features: [
                {type: 'Feature', geometry: {type: 'Point', coordinates: pt}}
            ]
        })
    }

    function buildLineString(pt) {
        return ({
            type: 'FeatureCollection',
            features: [
                {type: 'Feature', geometry: {type: 'LineString', coordinates: pt}}
            ]
        })
    }

    function handleSearchLon() {
        let G = getGraph();
        let rs = [];
        if(!G) return rs;
        rs = G.filter(x => x[0].toString().includes(lonSearch.toString())).map(x => x[0]);
        rs.splice(10);
        setLonSearchResults(rs);
    }

    useEffect(() => {
        handleSearchLon();
    }, [lonSearch]);

    function handleSearchLat() {
        let G = getGraph();
        let rs = [];
        if(!G) return rs;
        rs = G.filter(x => x[0].toString().includes(latSearch.toString())).map(x => x[0]);
        rs.splice(10);
        setLonSearchResults(rs);
    }

    useEffect(() => {
        handleSearchLat();
    }, [latSearch]);

    useEffect(() => {
        getRoute();
    }, [target]);

    useEffect(() => {
        loadFile();
    }, [selectedFile]);

    return (
        <div>
        {graphLoading ? <LinearProgress /> : null}
        <MapGL
            onViewportChange        = {handleChange}
            mapboxApiAccessToken    = {accessToken}
            onClick                 = {handleOnClick}
            {...viewport}
        >

            <Source id="graph2" type="geojson" data={toggleGraph ? graph : buildLineString([])}>
                <Layer
                    id="graph2"
                    type="circle"
                    paint={{
                        'circle-radius': viewport.zoom > 13 ? (viewport.zoom > 18 ? 5 : 2) : (viewport.zoom < 8 ? 0.5 : 1),
                        'circle-color': '#6c6c6c'
                    }}/>
            </Source>

            <Source id="visited" type="geojson" data={toggleVisited ? visited : buildLineString([])}>
                <Layer
                    id="visited"
                    type="circle"
                    paint={{
                        'circle-radius': viewport.zoom > 15 ? (viewport.zoom > 18 ? 5 : 4) : (viewport.zoom < 8 ? 0.5 : 1),
                        'circle-color': 'rgb(26,83,255)'
                    }}/>
            </Source>


            <Source id="routeFrom" type="geojson" data={routeFrom}>
                <Layer
                    id ="routeFrom"
                    type ="circle"
                    paint = {{
                        'circle-radius': 10,
                        'circle-color': '#00bf01'
                    }} />
            </Source>

            <Source id="routeTo" type="geojson" data={routeTo}>
                <Layer
                    id ="routeTo"
                    type ="circle"
                    paint = {{
                        'circle-radius': 10,
                        'circle-color': '#bf0004'
                    }} />
            </Source>

            <Source id="routePath" type="geojson" data={route}>
                <Layer
                    id ="route"
                    type ="line"
                    layout = {{
                        'line-join': 'round',
                        'line-cap': 'round'
                    }}
                    paint = {{
                        'line-color': '#ff00fa',
                        'line-width': 3
                    }} />
            </Source>

            {toggleLandmarks ?
                <Source id="landmarks" type="geojson" data={landmarks}>
                    <Layer
                        id="landmarks"
                        type="circle"
                        paint={{
                            'circle-radius': 8,
                            'circle-color': '#bfbc00'
                        }}/>
                </Source>
                :
                null
            }


{/*            <Source id="graph" type="geojson" data={graph}>
                <Layer
                    id ="graph"
                    type ="line"
                    layout = {{
                        'line-join': 'round',
                        'line-cap': 'round'
                    }}
                    paint = {{
                        'line-color': '#000000',
                        'line-width': 1
                    }} />
            </Source>*/}


            <Source id="nearest" type="geojson" data={nearest}>
                <Layer
                    id ="nearest"
                    type ="circle"
                    paint = {{
                        'circle-radius': 10,
                        'circle-color': '#0011bf'
                    }} />
            </Source>

        </MapGL>
        </div>
    );
}