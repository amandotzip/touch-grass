const express = require('express');
const path = require('path');
const app = express();

// Serve static files from the dist folder
app.use(express.static(path.join(__dirname, 'dist/touch-grass')));

// Redirect all other routes to the index.html
app.get('/*', function(req, res) {
  res.sendFile(path.join(__dirname, 'dist/touch-grass/browser/index.html'));
});

// Start the app by listening on the default Heroku port
app.listen(process.env.PORT || 8080, () => {
  console.log("Server is running...");
});