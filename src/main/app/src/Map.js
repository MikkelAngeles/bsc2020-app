import React, {useContext, useEffect, useState} from 'react';
import MapGL, {Layer, LinearInterpolator, WebMercatorViewport, Source} from 'react-map-gl';
import LinearProgress from "@material-ui/core/LinearProgress";
import {SelectionContext} from "./SelectionContext";
import CircularProgress from "@material-ui/core/CircularProgress";
import Backdrop from "@material-ui/core/Backdrop";
import {makeStyles} from "@material-ui/styles";
import {MapContext} from "./MapContext";

const accessToken = "pk.eyJ1IjoibWlra2VsYW5nZWxlcyIsImEiOiJjazltanR4YWUwMHhqM25xbTNjYTBidzFoIn0.NZCPthWITf6K5AeN8Xwn1Q";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: 5,
        color: '#fff',
    },
}));

export default function Map (props) {
    const model     = useContext(SelectionContext);
    const mapModel  = useContext(MapContext);
    const classes   = useStyles();

    const [viewport, setViewport] = useState({
            width: window.outerWidth,
            height: window.outerHeight + 50,
            latitude: 55.6,
            longitude:  10.7,
            zoom: 6
    });

    function handleChangeViewport(vp) {
        setViewport(vp);
    }

    function clear() {
        model.clearSelectedPoints();
        model.setSource(-1);
        model.setTarget(-1);
        mapModel.clearRoute();
    }

    function getGraph() {
        return mapModel.graph.vertices;
    }

    function handleOnClick(e) {
        let cur = e.lngLat;
        let cur_lng = cur[0];
        let cur_lat = cur[1];
        let G = getGraph();

        if(e && e.rightButton) {
            console.log(model.selectedPoints);
            if (model.selectedPoints.routeFrom.point.length === 0) {
                let rs =  findNearest(cur_lng, cur_lat, G);
                if(rs > -1) {
                    model.setRouteFrom(G[rs], rs);
                    model.setSource(rs);
                }
            }
            else if (model.selectedPoints.routeTo.point.length === 0) {
                let rs =  findNearest(cur_lng, cur_lat, G);
                if(rs > -1) {
                    model.setRouteTo(G[rs], rs);
                    model.setTarget(rs);
                }
            } else {
                clear();
            }
        } else if(e.leftButton) {
            let rs =  findNearest(cur_lng, cur_lat, G);
            if(rs > -1) {
                model.setNearest(G[rs], rs);
            }
        }
    }

    function findNearest(cur_lng, cur_lat, arr) {
        let result = Infinity;
        let nearest = -1;
        for (let i=0; i < arr.length;i++){
            let ans = haversine(cur_lat, arr[i][1], cur_lng, arr[i][0]);
            if (ans < result){
                result = ans;
                nearest = i;
            }
        }
        return nearest;
    }

    function toRad(Value) {
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

    function buildSymbols(coords) {
        let tmp = [];

        for(let i = 0; i < coords.length; i++) {
            let curr = coords[i];
            tmp.push(
                {
                    type: 'Feature',
                    properties: {
                        description: mapModel.edges[i].dist
                    },
                    geometry: {type: 'Point', coordinates: [curr[0], curr[1]]}
                }
            );
        }

        return ({
            type: 'FeatureCollection',
            features: tmp
        })
    }

    function buildCoordinates(type, coords) {
        return ({
            type: 'FeatureCollection',
            features: [
                {type: 'Feature', geometry: {type: type, coordinates: coords}}
            ]
        })
    }
    function buildPoint(points) {
        return buildCoordinates('Point', points)
    }

    function buildLineString(points) {
        return buildCoordinates('LineString', points)
    }

    function buildPolygon(points) {
        return buildCoordinates('Polygon', [points]);
    }

    function handleZoomToBounds() {
        let minLng = mapModel.graph.bounds.minX;
        let minLat = mapModel.graph.bounds.minY;
        let maxLng = mapModel.graph.bounds.maxX;
        let maxLat = mapModel.graph.bounds.maxY;
        zoomToRegion(minLng, minLat, maxLng, maxLat);
    }

    function zoomToRegion(minLng, minLat, maxLng, maxLat) {
        const vp = new WebMercatorViewport(viewport);
        const {longitude, latitude, zoom} = vp.fitBounds([[minLng, minLat], [maxLng, maxLat]], {
            padding: 200
        });
        setViewport({
            width: window.outerWidth,
            height: window.outerHeight + 50,
            latitude: latitude,
            longitude: longitude,
            zoom: zoom,
            transitionInterpolator: new LinearInterpolator({
                around: vp.center
            }),
            transitionDuration: 1000
        });
    }

    //Hook events
    useEffect(() => {
        //zoomEvent();
        handleZoomToBounds();
    }, [mapModel.graph]);

    useEffect(() => {
        function handleResize() {
            setViewport({...viewport, width: window.innerWidth, height: window.innerHeight});
        }
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    //Data source conversion to MapBox features
    const boundsHull            = getChecked("graphBounds", buildPolygon(mapModel.boundsHull));
    const vertices              = getChecked("vertices", buildLineString(mapModel.graph.vertices));
    const verticesHull          = getChecked("verticesHull", buildPolygon(mapModel.graph.verticesHull));
    const landmarks             = getChecked("landmarks", buildLineString(mapModel.graph.landmarks));
    const landmarksHull         = getChecked("landmarksHull", buildPolygon(mapModel.graph.landmarksHull));
    const visited               = getChecked("visited", buildLineString(mapModel.route.visited));
    const visitedHull           = getChecked("visitedHull", buildPolygon(mapModel.route.hull));
    const route                 = getChecked("route", buildLineString(mapModel.route.route));
    const routeFrom             = buildPoint(model.selectedPoints.routeFrom.point);
    const routeTo               = buildPoint(model.selectedPoints.routeTo.point);
    const nearestPoint          = buildPoint(model.selectedPoints.nearest.point);
    const nearestEdges          = buildLineString(mapModel.getNearestEdges());
    const nearestEdgePoints     = buildLineString(mapModel.getNearestEdgePoints());
    const edgeLabels            = buildSymbols(mapModel.getNearestEdgePoints());

    //Returns data source as MapBox feature if key is selected
    function getChecked(key, ref) {
        return model.checked.includes(key) ? ref : buildLineString([]);
    }

    return (
        <div>

            {mapModel.isLoading ?
                <div style={{position: 'absolute', width: '100%', zIndex: 100}}>
                    <LinearProgress />
                </div>
                :
                null}

            <Backdrop className={classes.backdrop} open={mapModel.isLoading}>
                <CircularProgress color="inherit" />
            </Backdrop>

            <MapGL
                onViewportChange        = {handleChangeViewport}
                mapboxApiAccessToken    = {accessToken}
                onClick                 = {handleOnClick}
                {...viewport}
            >
                <Source id="graphBounds" type="geojson" data={boundsHull}>
                    <Layer
                        id="graphBounds"
                        type ="fill"
                        paint = {{
                            'fill-color': 'rgb(26,255,0)',
                            'fill-opacity': 0.2,
                            'fill-outline-color': 'rgb(0,128,1)',
                        }} />
                </Source>

                <Source id="verticesRegion" type="geojson" data={verticesHull}>
                    <Layer
                        id="verticesRegion"
                        type ="fill"
                        paint = {{
                            'fill-color': '#313131',
                            'fill-opacity': 0.2,
                            'fill-outline-color': '#3f3f3f',
                        }} />
                </Source>

                <Source id="landmarksRegion" type="geojson" data={landmarksHull}>
                    <Layer
                        id="landmarksRegion"
                        type ="fill"
                        paint = {{
                            'fill-color': '#bfa300',
                            'fill-opacity': 0.2,
                            'fill-outline-color': '#bf3b00',
                        }} />
                </Source>

                <Source id="vertices" type="geojson" data={vertices}>
                    <Layer
                        id="vertices"
                        type="circle"
                        paint={{
                            'circle-radius': viewport.zoom > 13 ? (viewport.zoom > 18 ? 5 : 2) : (viewport.zoom < 8 ? 0.5 : 1),
                            'circle-color': '#6c6c6c',
                            'circle-opacity': 0.6,
                        }}/>
                </Source>

                <Source id="visited" type="geojson" data={visited}>
                    <Layer
                        id="visited"
                        type="circle"
                        paint={{
                            'circle-radius': viewport.zoom > 13 ? (viewport.zoom > 18 ? 5 : 2) : (viewport.zoom < 8 ? 0.5 : 1),
                            'circle-color': 'rgb(26,83,255)',
                            'circle-opacity': 0.6,
                        }}/>
                </Source>

                <Source id="visitedRegion" type="geojson" data={visitedHull}>
                    <Layer
                        id="visitedRegion"
                        type ="fill"
                        paint = {{
                            'fill-color': 'rgb(26,83,255)',
                            'fill-opacity': 0.6,
                            'fill-antialias': false,
                            'fill-outline-color': 'rgb(1,0,255)',
                        }} />
                </Source>

                <Source id="routeFrom" type="geojson" data={routeFrom}>
                    <Layer
                        id ="routeFrom"
                        type ="circle"
                        paint = {{
                            'circle-radius': 10,
                            'circle-color': '#00bf01',
                            'circle-opacity': 0.6,
                        }} />
                </Source>

                <Source id="routeTo" type="geojson" data={routeTo}>
                    <Layer
                        id ="routeTo"
                        type ="circle"
                        paint = {{
                            'circle-radius': 10,
                            'circle-color': '#bf0004',
                            'circle-opacity': 0.6,
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
                            'line-width': 3,
                            'line-opacity': 0.6,
                        }} />
                </Source>

                <Source id="landmarks" type="geojson" data={landmarks}>
                    <Layer
                        id="landmarks"
                        type="circle"
                        paint={{
                            'circle-radius': 8,
                            'circle-color': '#bfbc00',
                            'circle-opacity': 0.6,
                        }}/>
                </Source>

                <Source id="nearestEdges" type="geojson" data={nearestEdges}>
                    <Layer
                        id ="nearestEdges"
                        type ="line"
                        layout = {{
                            'line-join': 'round',
                            'line-cap': 'round'
                        }}
                        paint = {{
                            'line-color': '#ff2c00',
                            'line-width': 3,
                            'line-opacity': 0.6,
                        }} />
                </Source>

                <Source id="edgeLabels" type="geojson" data={edgeLabels}>
                    <Layer
                        id ="edgeLabels"
                        type ="symbol"
                        layout = {{
                            'text-field': ['get', 'description'],
                            'text-variable-anchor': ['top', 'bottom', 'left', 'right'],
                            'text-radial-offset': 0.5,
                            'text-justify': 'auto',
                            'icon-image': ['concat', ['get', 'icon'], '-15']
                        }}
                    />
                </Source>

                <Source id="nearest" type="geojson" data={nearestPoint}>
                    <Layer
                        id ="nearest"
                        type ="circle"
                        paint = {{
                            'circle-radius': 10,
                            'circle-color': '#0011bf',
                            'circle-opacity': 0.6,
                        }} />
                </Source>

                <Source id="nearestEdgePoints" type="geojson" data={nearestEdgePoints}>
                    <Layer
                        id ="nearestEdgePoints"
                        type ="circle"
                        paint = {{
                            'circle-radius': 5,
                            'circle-color': '#bf0004',
                            'circle-opacity': 0.6,
                        }} />
                </Source>

            </MapGL>
        </div>
    );
}