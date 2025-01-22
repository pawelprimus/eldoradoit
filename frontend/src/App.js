import { useState, useEffect } from 'react';
import { 
  Container, 
  Typography, 
  Box, 
  AppBar, 
  Toolbar, 
  Paper 
} from '@mui/material';
import axios from 'axios';
import config from './config';

function App() {
  const [message, setMessage] = useState('');
  const [hits, setHits] = useState([]);

  useEffect(() => {
    // Using the API URL from config
    axios.get(`${config.apiUrl}/api/hello`)
      .then(response => setMessage(response.data))
      .catch(error => console.error('Error:', error));

    // Fetching hits from the new endpoint
    axios.get(`${config.apiUrl}/api/hits`)
      .then(response => setHits(response.data))
      .catch(error => console.error('Error fetching hits:', error));
  }, []);

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6">
            Eldorado
          </Typography>
        </Toolbar>
      </AppBar>
      
      <Container maxWidth="lg">
        <Box sx={{ mt: 4 }}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
              Hello World
            </Typography>
            <Typography variant="body1">
              Backend response: {message}
            </Typography>
            <Typography variant="h5" gutterBottom>
              Hits:
            </Typography>
            <Box sx={{ maxHeight: '300px', overflowY: 'auto' }}>
              <ul>
                {hits.map((hit, index) => (
                  <li key={index}>{hit}</li>
                ))}
              </ul>
            </Box>
          </Paper>
        </Box>
      </Container>
    </>
  );
}

export default App;