const proxy = require('http-proxy-middleware');

module.exports = function(app) {
    if(process.env.NODE_ENV === 'development') {
        app.use(proxy('/api', { target: 'http://localhost:8080', changeOrigin: true }));
    }
};