import * as React from 'react';
import Paper from '@material-ui/core/Paper';
import {
    Chart,
    ScatterSeries,
    ArgumentAxis,
    ValueAxis,
    Title,
    Legend
} from '@devexpress/dx-react-chart-material-ui';

import {Animation } from '@devexpress/dx-react-chart';
import {useState} from "react";
import {MapContext} from "./MapContext";
import {useContext} from "react";
import {useEffect} from "react";
import {algorithms} from "./selectionProperties";

export default function HistoryChart () {
    const [chartData, setChartData] = useState([]);
    const model = useContext(MapContext);
    const algs = algorithms;

    function setData() {
        let rs = [];
        for(let i = 0; i < model.results.length; i++) {
            let cur = model.results[i];
            if(!cur || !cur.data.hasPath) continue;
            let ms = parseFloat((cur.data.elapsed / 1000000).toFixed(0));
            let dist = parseFloat(cur.data.dist); //cur.data.dist ? cur.data.dist.toFixed(0) : 0;
            if (cur.algorithm === "Dijkstra") rs.push({elapsed0: ms, dist0: dist, elapsed1: 0.0, dist1: 0.0, elapsed2: 0.0, dist2: 0.0});
            else if (cur.algorithm === "A*") rs.push({elapsed0: 0.0, dist0: 0.0, elapsed1: ms, dist1: dist, elapsed2: 0.0, dist2: 0.0});
            else if (cur.algorithm === "A* Landmarks") rs.push({elapsed0: 0.0, dist0: 0.0, elapsed1: 0.0, dist1: 0.0, elapsed2: ms, dist2: dist});
        }
        console.log(rs);
        setChartData(rs);
    }
    useEffect(() => {
        setData();
    }, [model]);

    return (
        <Paper>
            <Chart
                data = {chartData}
            >
                <ArgumentAxis showGrid />
                <ValueAxis />
                 {
                    algs.map((v, k) =>
                        <ScatterSeries
                            name            = {v.file}
                            valueField      = {"dist"+v.id}
                            argumentField   = {"elapsed"+v.id}
                        />
                    )
                }
                <Animation />
                <Legend position="bottom" />
                <Title
                    text="SP Algorithm effeciency distance vs elapsed"
                />
            </Chart>
        </Paper>
    )
}
