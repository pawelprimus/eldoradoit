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

function App() {
  const [message, setMessage] = useState('');

  useEffect(() => {
    // This will call your backend API
    axios.get('http://localhost:8080/api/hello')
      .then(response => setMessage(response.data))
      .catch(error => console.error('Error:', error));
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
          </Paper>
        </Box>
      </Container>
    </>
  );
}

export default App;