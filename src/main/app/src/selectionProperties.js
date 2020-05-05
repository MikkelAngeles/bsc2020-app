export function graphTitle(id) {
    return graphs[id].file + "  (" + graphs[id].type + ")";
}
export function graphUrl(id) {
    return graphs[id].url;
}
export const graphs = [
    {
        id: 0,
        file: 'Hillerød',
        type: 'GeoJSON - OSM',
        url: '/load/json/hil',
        vertices: 29711,
        edges: 80454,
        bytes: 3.7
    },
    {
        id: 1,
        file: 'New York City',
        type: 'DIMACS',
        url: '/load/dimacs?path=nyc',
        vertices: '264,346',
        edges: '733,846',
        bytes: (2 + 3.6 + 3.5)
    },
    {
        id: 2,
        file: 'Florida',
        type: 'DIMACS',
        url: '/load/dimacs?path=fla',
        vertices: '1,070,376',
        edges: '2,712,798',
        bytes: (14 + 14 + 8.6)
    },
    /*
    {
        id: 3,
        file: 'California & Nevada',
        type: 'DIMACS',
        url: '/dimacs/nyc',
        vertices: '1,890,815',
        edges: '4,657,742',
        bytes: (26 + 26 + 16)
    },
    {
        id: 4,
        file: 'Eastern USA',
        type: 'DIMACS',
        url: '/dimacs/nyc',
        vertices: '3,598,623',
        edges: '8,778,114',
        bytes: (49 + 50 + 32)
    },
    {
        id: 5,
        file: 'Western USA',
        type: 'DIMACS',
        url: '/dimacs/nyc',
        vertices: '6,262,104',
        edges: '15,248,146',
        bytes: (86 + 88 + 57)
    },
    {
        id: 6,
        file: 'Full USA',
        type: 'DIMACS',
        url: '/dimacs/nyc',
        vertices: '23,947,347',
        edges: '58,333,344',
        bytes: (335 + 342 + 218)
    }*/
];

export function algorithmTitle(id) {
    return algorithms[id].file;
}
export function algorithmUrl(id) {
    return algorithms[id].url;
}
export const algorithms = [
    {
        id: 0,
        file: 'Dijkstra',
        url: '/route/dijkstra',
        type: 'Mikkel Helmersen',
    },
    {
        id: 1,
        file: 'A*',
        url: '/route/astar',
        type: 'Mikkel Helmersen',
    },
    {
        id: 2,
        file: 'A* Landmarks',
        url: '/route/astar-landmarks',
        type: 'Mikkel Helmersen',
    },
];