import React, {useContext, useEffect, useState} from 'react';
import {SelectionContext} from "./SelectionContext";
import axios from "axios";
import {AlertContext} from "./AlertContext";
import {algorithmTitle, algorithmUrl} from "./selectionProperties";

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
    const [selectedGraph, setSelectedGraph]             = useState("");
    const [selectedAlgorithm, setSelectedAlgorithm]     = useState(0);
    const [graph, setGraph]                             = useState(graphTemplate);
    const [route, setRoute]                             = useState(routeTemplate);
    const [isLoading, setIsLoading]                     = useState(false);
    const [edges, setEdges]                             = useState([]);
    const [properties, setProperties]                   = useState({});
    const [results, setResults]                         = useState([]);
    const [models, setModels]                            = useState([]);

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
        if(isLoading || selectedGraph === "") return;
        setIsLoading(true);
        alertModel.info("Loading " + selectedGraph + "");
        selectionModel.clearSelectedPoints();
        selectionModel.clearSelectedCriteria();
        let path = baseUrl + "/model/load?modelName="+ selectedGraph;
        setGraph(graphTemplate);
        setResults([]);
        axios.get(path)
            .then((response) => {
                if(response.status === 200 && response.data) {
                    let data = response.data;
                    setGraph(data);
                    alertModel.success("Successfully loaded " + selectedGraph + "!");
                    loadProperties();
                }
                console.log(response);
            })
            .catch((e) => alertModel.error(e.toString()))
            .finally(() => setIsLoading(false))
    }

    function loadProperties() {
        setIsLoading(true);
        alertModel.info("Loading properties..");

        axios.get(baseUrl + "/model/properties")
            .then((response) => {
                if(response.status === 200 && response.data) {
                    let data = response.data;
                    setProperties(data);
                    alertModel.success("Successfully loaded properties");
                }
                console.log(response);
            })
            .catch((e) => alertModel.error(e.toString()))
            .finally(() => setIsLoading(false))
    }

    function loadModels() {
        alertModel.info("Loading list of models..");

        axios.get(baseUrl + "/model/all")
            .then((response) => {
                if(response.status === 200 && response.data) {
                    let data = response.data;
                    setModels(data);
                    alertModel.success("Successfully loaded list of models");
                    if(data.length > 0) setSelectedGraph(data[0].fileName);
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
        if(selectionModel.criteria.length > 0) alg += "/criteria";
        let path = `${alg}?from=${q.source}&to=${q.target}`;
        if(selectedAlgorithm === 1 || selectedAlgorithm === 2) path += `&heuristic=${q.heuristicsWeight}`;
        if(selectionModel.criteria.length > 0) path += "&criteria="+encodeURI(JSON.stringify(selectionModel.criteria));

        let resultTemplate = {
            from: q.source,
            to: q.target,
            graph: selectedGraph,
            algorithm: algorithmTitle(selectedAlgorithm),
            data: routeTemplate,
            criteria: [selectionModel.criteria]
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

    useEffect(() => {
        if(models.length === 0) loadModels();
    }, [models]);


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
        results,
        properties,
        models
    }
}