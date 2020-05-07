import React, {useState} from 'react';

export const criterionTemplate = {weightType:"DISTANCE",property:{key:"",value:""},weightFactor:1.0};
export function useSelectionModel (props) {
    const [query, setQuery] = useState({
        source: -1,
        target: -1,
        criteria: [],
        heuristicsWeight: 1
    });
    const [selectedPoints, setSelectedPoints] = useState({
        nearest:    {index: -1, point: []},
        routeFrom:  {index: -1, point: []},
        routeTo:    {index: -1, point: []}
    });

    const [checked, setChecked] = useState(['route', 'verticesHull']);

    function handleHeuristicWeight(v) {
        setQuery({...query, heuristicsWeight: v});
    }
    function handleSetSource(v) {
        setQuery({...query, source: v});
    }
    function handleSetTarget(v) {
        setQuery({...query, target: v});
    }

    function handleSetNearest(pt, index) {
        setSelectedPoints({...selectedPoints, nearest: {index: index, point: pt}});
    }
    function handleSetRouteFrom(pt, index)  {
        setSelectedPoints({...selectedPoints, routeFrom: {index: index, point: pt}});
        handleSetSource(index);
    }
    function handleSetRouteTo(pt, index) {
        setSelectedPoints({...selectedPoints, routeTo: {index: index, point: pt}});
        handleSetTarget(index);
    }

    function clearSelectedPoints() {
        setSelectedPoints({
            nearest:    {index: -1, point: []},
            routeFrom:  {index: -1, point: []},
            routeTo:    {index: -1, point: []}
        });
        setQuery({...query, source: -1, target: -1});
    }

    function handleAddCriterion(c) {
          setQuery({...query, criteria: [...query.criteria, c]});
    }

    function handleRemoveCriterion(key, value) {
        let rest = query.criteria.filter(e => e.property.key !== key || (e.property.key === key && e.property.value !== value));
        setQuery({...query, criteria: rest});
    }

    return {
        query,
        checked,
        setChecked,
        selectedPoints,
        clearSelectedPoints,
        heuristicsWeight: query.heuristicsWeight,
        setHeuristicsWeight: handleHeuristicWeight,
        setSource: handleSetSource,
        setTarget: handleSetTarget,
        setRouteFrom: handleSetRouteFrom,
        setRouteTo: handleSetRouteTo,
        setNearest: handleSetNearest,
        criteria: query.criteria,
        addCriterion: handleAddCriterion,
        removeCriterion: handleRemoveCriterion
    }
}