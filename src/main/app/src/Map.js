import React, {useState} from 'react';
import MapGL, {Layer, Source} from 'react-map-gl';

const accessToken = "pk.eyJ1IjoibWlra2VsYW5nZWxlcyIsImEiOiJjazltanR4YWUwMHhqM25xbTNjYTBidzFoIn0.NZCPthWITf6K5AeN8Xwn1Q";

export default function Map () {
    const [viewport, setViewport] = useState({
            width: 800,
            height: 800,
            latitude: 55.6,
            longitude:  10.7,
            zoom: 6
    });

    const [routeFrom, setRouteFrom] = useState(buildPoint([]));
    const [routeTo, setRouteTo] = useState(buildPoint([]));
    const [route, setRoute] = useState(buildPoint([]));

    const [landmarks, setLandmarks] = useState(buildLineString([
            [10.579150390623285,57.72461531315225],
            [12.282031249997033,56.09954580268264],
            [10.732958984373925,54.68328220960388],
            [8.129199218748925,55.60620642188443],
            [11.534960937498278,55.36967133830027],
            [9.370654296873628,56.22190373231677]
    ]));

    const [graph, setGraph] = useState(buildLineString([

    ]));

    const [visited, setVisited] = useState(buildLineString([

    ]));

    function handleChange(vp) { setViewport(vp); }

    function clear() {
        let point = buildPoint([]);
        setRoute(buildLineString([]));
        setRouteFrom(point);
        setRouteTo(point);
    }

    function handleOnClick(e) {
        if(e && !e.rightButton) return;
        console.log(e.lngLat);
        let point = buildPoint(e.lngLat);

        if(routeFrom.features[0].geometry.coordinates.length === 0) setRouteFrom(point);
        else if(routeTo.features[0].geometry.coordinates.length === 0) {
            setRouteTo(point);
            calculateRoute();
        }
        else {
            clear();
        }
    }

    function calculateRoute() {
        setRoute(buildLineString([
            [10.7, 55.6],
            [10.8, 55.6],
            [10.9, 55.6]
        ]))
    }

    function goTo(lon, lat) {
        setViewport({
            width: 800,
            height: 800,
            latitude: lat,
            longitude: lon,
            zoom: 8
        });
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

    function addLine(lon1, lat1, lon2, lat2) {

    }

    return (
        <>
        <MapGL
            onViewportChange        = {handleChange}
            mapboxApiAccessToken    = {accessToken}
            onPointerMove           = {console.log("onPointerMove")}
            onClick                 = {handleOnClick}
            {...viewport}
        >

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
                    id ="routePath"
                    type ="circle"
                    paint = {{
                        'circle-radius': 5,
                        'circle-color': '#bf00a3'
                    }} />
            </Source>

            <Source id="landmarks" type="geojson" data={landmarks}>
                <Layer
                    id ="landmarks"
                    type ="circle"
                    paint = {{
                        'circle-radius': 5,
                        'circle-color': '#bfa300'
                    }} />
            </Source>


        </MapGL>
            routeFrom: {JSON.stringify(routeFrom)} - routeTo: {JSON.stringify(routeTo)}
        </>
    );
}