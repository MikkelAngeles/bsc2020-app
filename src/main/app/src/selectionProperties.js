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