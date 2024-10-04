const express = require('express');
const path = require('path');
const app = express();

// Serve the static files from the Angular dist directory
app.use(express.static(path.join(__dirname, 'dist/touch-grass/browser')));

// Redirect all other routes to index.html so that Angular can handle client-side routing
app.get('/*', function (req, res) {
  res.sendFile(path.join(__dirname, 'dist/touch-grass/browser/index.html'));
});

// Start the app by listening on the default Heroku port or on port 8080 locally
const port = process.env.PORT || 8080;
app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
