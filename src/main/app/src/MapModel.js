import React, {useContext, useEffect, useState} from 'react';
import {SelectionContext} from "./SelectionContext";
import axios from "axios";
import {AlertContext} from "./AlertContext";
import {algorithmTitle, algorithmUrl, graphTitle, graphUrl} from "./selectionProperties";

const baseUrl = 'http://localhost:8080';

const routeTemplate = {
    elapsed: 0,
    dist: 0,
    hasPath: false,
    visited: [],
    route: [],
    hull: []
};
const graphTemplate = {
    V:0, E:0, bounds: {minX:0, maxX:0, minY:0, maxY:0}, landmarks: [], landmarksHull: [], vertices: [], verticesHull: []
};

export function useMapModel (props) {
    const selectionModel                                = useContext(SelectionContext);
    const alertModel                                    = useContext(AlertContext);
    const [selectedGraph, setSelectedGraph]             = useState(0);
    const [selectedAlgorithm, setSelectedAlgorithm]     = useState(0);
    const [graph, setGraph]                             = useState(graphTemplate);
    const [route, setRoute]                             = useState(routeTemplate);
    const [isLoading, setIsLoading]                     = useState(false);
    const [edges, setEdges]                             = useState([]);
    const [results, setResults]                         = useState([]);

    const boundsHull = [
        [graph.bounds.minX, graph.bounds.minY],
        [graph.bounds.maxX, graph.bounds.minY],
        [graph.bounds.maxX, graph.bounds.maxY],
        [graph.bounds.minX, graph.bounds.maxY]
    ];

    function handleClearRoute() {
        setRoute(routeTemplate);
    }

    function getNearestEdgePoints() {
        let rs = [];
        for(let i = 0; i < edges.length; i++) {
            let curr = edges[i];
            rs.push(graph.vertices[curr.w])
        }
       return rs;
    }

    function getNearestEdges() {
        let rs = [];
        for(let i = 0; i < edges.length; i++) {
            let curr = edges[i];
            rs.push(graph.vertices[curr.v]);
            rs.push(graph.vertices[curr.w])
        }
        return rs;
    }

    function loadEdgesByVertex(v) {
        if(isLoading) return;
        setIsLoading(true);
        axios.get(baseUrl + "/edges?vertex=" + v)
            .then((response) => {
                if(response.status === 200 && response.data) {
                    let data = response.data;
                    setEdges(data);
                }
                console.log(response);
            })
            .catch((e) => alertModel.error(e.toString()))
            .finally(() => setIsLoading(false))
    }

    function loadGraph() {
        if(isLoading) return;
        setIsLoading(true);
        alertModel.info("Loading " + graphTitle(selectedGraph) + "");
        selectionModel.clearSelectedPoints();

        let path = baseUrl + graphUrl(selectedGraph);
        setGraph(graphTemplate);
        setResults([]);
        axios.get(path)
            .then((response) => {
                if(response.status === 200 && response.data) {
                    let data = response.data;
                    setGraph(data);
                    alertModel.success("Successfully loaded " + graphTitle(selectedGraph) + "!");
                }
                console.log(response);
            })
            .catch((e) => alertModel.error(e.toString()))
            .finally(() => setIsLoading(false))
    }

    function getRoute() {
        if(isLoading) return;
        let q = selectionModel.query;
        if(q.source === -1 || q.target === -1) {
            alertModel.info("Source and target must be selected");
            return;
        }
        setIsLoading(true);

        let alg = algorithmUrl(selectedAlgorithm);
        let path = `${alg}?from=${q.source}&to=${q.target}`;
        if(selectedAlgorithm === 1 || selectedAlgorithm === 2) path += `&heuristic=${q.heuristicsWeight}`;

        let resultTemplate = {
            from: q.source,
            to: q.target,
            graph: graphTitle(selectedGraph),
            algorithm: algorithmTitle(selectedAlgorithm),
            data: routeTemplate
        };

        axios.get(`${baseUrl}/${path}`)
            .then((response) => {
                if(response.status === 200 && response.data) {
                    let data = response.data;
                    setRoute(data);
                    resultTemplate.data = data;
                    setResults([...results, resultTemplate]);
                    alertModel.success("Route calculated in " + (data.elapsed / 1000000) + " ms, distance " + data.dist.toFixed(4));
                }
            })
            .catch((e) => {
                console.log(e);
                alertModel.error(e.toString());
                setResults([...results, resultTemplate]);
            })
            .finally(() => setIsLoading(false))
    }

    useEffect(() => {
        getRoute();
    }, [selectionModel.query.target]);

    useEffect(() => {
        loadGraph();
        setEdges([]);
    }, [selectedGraph]);

    useEffect(() => {
        const vertex = selectionModel.selectedPoints.nearest.index;
        if(vertex !== -1) loadEdgesByVertex(vertex);
        else setEdges([]);
    }, [selectionModel.selectedPoints.nearest]);


    return {
        graph,
        route,
        isLoading,
        selectedAlgorithm,
        setSelectedAlgorithm,
        selectedGraph,
        setSelectedGraph,
        bounds: graph.bounds,
        boundsHull,
        clearRoute: handleClearRoute,
        calculateRoute: getRoute,
        edges,
        getNearestEdgePoints,
        getNearestEdges,
        results
    }
}