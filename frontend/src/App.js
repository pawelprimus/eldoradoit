import { useEffect, useState } from 'react';
import axios from 'axios';
import config from './config';
import chartOptions from './chartOptions';
import CustomChart from './CustomChart';

function App() {
  const [offerData, setOfferData] = useState([]);
  const [levelData, setLevelData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [lastUpdated, setLastUpdated] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true);
        setError(null);
        
        const responseLastUpdated = await axios.get(`${config.apiUrl}/trigger/newest`);
        const lastUpdatedTime = responseLastUpdated.data;
        setLastUpdated(formatDate(lastUpdatedTime));

        const responseOffers = await axios.get(`${config.apiUrl}/all`);
        const formattedOfferData = responseOffers.data.map(item => ({
          x: new Date(item.date).getTime(),
          y: item.count
        }));
        
        setOfferData([{
          name: 'Offers',
          data: formattedOfferData
        }]);

        const responseLevels = await axios.get(`${config.apiUrl}/levels`);
        const formattedLevelData = responseLevels.data.map(item => ({
          x: new Date(item.fetchDate).getTime(),
          ...item.offerCounts
        }));

        const seriesData = [
          {
            name: 'C_LEVEL',
            data: formattedLevelData.map(item => ({ x: item.x, y: item.C_LEVEL }))
          },
          {
            name: 'SENIOR',
            data: formattedLevelData.map(item => ({ x: item.x, y: item.SENIOR }))
          },
          {
            name: 'MID',
            data: formattedLevelData.map(item => ({ x: item.x, y: item.MID }))
          },
          {
            name: 'JUNIOR',
            data: formattedLevelData.map(item => ({ x: item.x, y: item.JUNIOR }))
          }
        ];

        setLevelData(seriesData);
      } catch (error) {
        setError('Coś nie bangla.');
        console.error('Error fetching data:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);

  const formatDate = (localDateTime) => {
    const date = new Date(localDateTime);
    return date.toLocaleString();
  };

  return (
    <div style={{
      padding: '40px',
      display: 'flex',
      justifyContent: 'center',
      backgroundColor: '#f5f5f5',
      minHeight: '100vh'
    }}>
      <div style={{
        width: '90%',
        maxWidth: '1400px',
        backgroundColor: 'white',
        borderRadius: '12px',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
        padding: '24px',
      }}>
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '24px'
        }}>
          <h1 style={{
            color: '#333',
            fontSize: '24px',
            fontWeight: '500',
            margin: 0
          }}>
            Czy to koniec eldorado?
          </h1>
          <div style={{
            color: '#666',
            fontSize: '14px'
          }}>
            Last updated: {lastUpdated}
          </div>
        </div>

        {isLoading && (
          <div style={{
            textAlign: 'center',
            padding: '40px',
            color: '#666'
          }}>
            Loading data...
          </div>
        )}

        {error && (
          <div style={{
            textAlign: 'center',
            padding: '40px',
            color: '#dc3545',
            backgroundColor: '#f8d7da',
            borderRadius: '8px'
          }}>
            {error}
          </div>
        )}

        {!isLoading && !error && (
          <>
            <CustomChart
              options={chartOptions}
              series={offerData}
              title="Liczba ofert w czasie"
            />
            <div style={{ margin: '40px 0' }} />
            <CustomChart
              options={{
                ...chartOptions,
                title: {
                  text: 'Liczba ofert z poszczególnych poziomów',
                  align: 'left',
                },
              }}
              series={levelData}
              title="Liczba ofert z poszczególnych poziomów"
            />
          </>
        )}
      </div>
    </div>
  );
}

export default App;
