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
  const [selectedTechnology, setSelectedTechnology] = useState('');
  const [selectedCity, setSelectedCity] = useState('');

  // Log filter changes
  useEffect(() => {
    if (selectedTechnology || selectedCity) {
      console.log('Active filters:', {
        technology: selectedTechnology || 'All',
        city: selectedCity || 'All'
      });
    }
  }, [selectedTechnology, selectedCity]);

  // Function to fetch data based on current filters
  const fetchData = async (technology = '', city = '') => {
    try {
      setIsLoading(true);
      setError(null);
      
      const responseLastUpdated = await axios.get(`${config.apiUrl}/trigger/newest`);
      const lastUpdatedTime = responseLastUpdated.data;
      setLastUpdated(formatDate(lastUpdatedTime));

      let responseLevels;
      
             if (technology && city) {
         // Fetch filtered data by both technology and city
         responseLevels = await axios.get(`${config.apiUrl}/levels/city/${city}/technology/${technology}`);
         console.log('Filtered Data Response:', responseLevels.data);
       } else if (technology) {
         // Fetch filtered data by technology only
         responseLevels = await axios.get(`${config.apiUrl}/levels/city/ALL/technology/${technology}`);
         console.log('Technology Filtered Data Response:', responseLevels.data);
       } else if (city) {
         // Fetch filtered data by city only
         responseLevels = await axios.get(`${config.apiUrl}/levels/city/${city}/technology/ALL`);
         console.log('City Filtered Data Response:', responseLevels.data);
       } else {
         // Fetch all data (no filters) - use the new endpoint with ALL for both
         responseLevels = await axios.get(`${config.apiUrl}/levels/city/ALL/technology/ALL`);
         console.log('All Data Response:', responseLevels.data);
       }

      const formattedLevelData = responseLevels.data.map(item => {
        if (item.offerCounts) {
          return {
            x: new Date(item.fetchDate).getTime(),
            ...item.offerCounts
          };
        } else {
          console.warn('Missing offerCounts for item:', item);
          return {
            x: new Date(item.fetchDate).getTime(),
            C_LEVEL: 0,
            SENIOR: 0,
            MID: 0,
            JUNIOR: 0
          };
        }
      });

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

      const allOffersData = formattedLevelData.map(item => ({
        x: item.x,
        y: item.ALL
      }));

      setOfferData([{
        name: 'Offers',
        data: allOffersData
      }]);

    } catch (error) {
      setError('Coś nie bangla.');
      console.error('Error fetching data:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // Initial data fetch
  useEffect(() => {
    fetchData();
  }, []);

  // Fetch data when filters change
  useEffect(() => {
    if (selectedTechnology || selectedCity) {
      fetchData(selectedTechnology, selectedCity);
    } else {
      fetchData(); // Fetch all data when no filters
    }
  }, [selectedTechnology, selectedCity]);

  const formatDate = (localDateTime) => {
    const date = new Date(localDateTime);
    return date.toLocaleString();
  };



  return (
    <div style={{
      padding: '12px',
      display: 'flex',
      justifyContent: 'center',
      backgroundColor: '#f8fafc',
      height: '100vh',
      overflow: 'hidden'
    }}>
                       <style>
          {`
            @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');
            
            .apexcharts-canvas {
              margin: 0 !important;
              padding: 0 !important;
            }
            .apexcharts-svg {
              margin: 0 !important;
              padding: 0 !important;
            }
            .apexcharts-inner {
              margin: 0 !important;
              padding: 0 !important;
            }
            .apexcharts-graphical {
              margin: 0 !important;
              padding: 0 !important;
            }
            .apexcharts-plot-area {
               margin: 0 !important;
               padding: 0 !important;
             }
             .apexcharts-container {
               height: 100% !important;
               max-height: 100% !important;
               overflow: hidden !important;
             }
             .apexcharts-canvas {
               height: 100% !important;
               max-height: 100% !important;
               overflow: hidden !important;
             }
             .apexcharts-svg {
               height: 100% !important;
               max-height: 100% !important;
               overflow: hidden !important;
             }
             .apexcharts-inner {
               height: 100% !important;
               max-height: 100% !important;
               overflow: hidden !important;
             }
             .apexcharts-graphical {
               height: 100% !important;
               max-height: 100% !important;
               overflow: hidden !important;
             }
             .apexcharts-plot-area {
               height: 100% !important;
               max-height: 100% !important;
               overflow: hidden !important;
             }
             .apexcharts-yaxis {
               overflow: hidden !important;
             }
                           .apexcharts-xaxis {
                overflow: hidden !important;
              }
              .apexcharts-title-text {
                margin-bottom: 30px !important;
                padding-right: 80px !important;
              }
              .apexcharts-toolbar {
                margin-top: 20px !important;
                position: absolute !important;
                top: 0 !important;
                right: 0 !important;
              }
              body, html {
                overflow: hidden !important;
              }
              * {
                box-sizing: border-box;
              }
             
                           /* Mobile Responsiveness */
              @media (max-width: 768px) {
                .mobile-container {
                  width: 98% !important;
                  padding: 12px !important;
                  height: calc(100vh - 24px) !important;
                }
                
                .mobile-header {
                  flex-direction: column !important;
                  align-items: stretch !important;
                  gap: 16px !important;
                }
                
                .mobile-filters {
                  flex-direction: column !important;
                  align-items: stretch !important;
                  gap: 16px !important;
                }
                
                .mobile-title {
                  font-size: 18px !important;
                  text-align: center !important;
                }
                
                .mobile-select {
                  min-width: 100% !important;
                  height: 44px !important;
                  font-size: 16px !important;
                }
                
                .mobile-badge {
                  font-size: 12px !important;
                  padding: 8px 16px !important;
                  text-align: center !important;
                }
                
                                 .mobile-chart {
                   margin-top: 12px !important;
                   max-height: calc(100vh - 280px) !important;
                   overflow-y: auto !important;
                 }
                
                .mobile-footer {
                  padding: 16px 0 !important;
                  font-size: 12px !important;
                }
              }
          `}
        </style>
             <div 
         className="mobile-container"
         style={{
         width: '95%',
         maxWidth: '1400px',
         backgroundColor: 'white',
         borderRadius: '16px',
         boxShadow: '0 10px 25px rgba(0, 0, 0, 0.08)',
         padding: '16px',
         height: 'calc(100vh - 48px)',
         display: 'grid',
         gridTemplateRows: 'auto 1fr auto',
         gridTemplateAreas: '"header" "chart" "footer"'
       }}>
                 <div 
           className="mobile-header"
           style={{
           display: 'flex',
           justifyContent: 'space-between',
           alignItems: 'center',
           marginBottom: '16px',
           padding: '0 2px',
           gridArea: 'header'
         }}>
                     <h1 
             className="mobile-title"
             style={{
             color: '#1e293b',
             fontSize: '22px',
             fontWeight: '600',
             margin: 0,
             letterSpacing: '-0.025em',
             fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif",
             textAlign: 'center'
           }}>
             Eldorado w IT? - analiza ofert pracy z justjoin.it
           </h1>
                     <div 
             className="mobile-filters"
             style={{
             display: 'flex',
             alignItems: 'center',
             gap: '12px'
           }}>
            <div style={{
              display: 'flex',
              flexDirection: 'column',
              gap: '4px',
              '@media (max-width: 768px)': {
                gap: '6px'
              }
            }}>
              <label style={{
                fontSize: '11px',
                color: '#64748b',
                fontWeight: '500',
                textTransform: 'uppercase',
                letterSpacing: '0.5px'
              }}>
                Technologia
              </label>
                             <select 
                                   className="mobile-select"
                                   style={{
                    padding: '8px 12px',
                    border: `1px solid ${selectedTechnology ? '#10b981' : '#e2e8f0'}`,
                    borderRadius: '6px',
                    fontSize: '13px',
                    backgroundColor: 'white',
                    color: '#1e293b',
                    minWidth: '120px',
                    cursor: 'pointer',
                    transition: 'all 0.2s ease',
                    outline: 'none',
                    height: '36px'
                  }}
                 value={selectedTechnology}
                 onChange={(e) => setSelectedTechnology(e.target.value)}
                 onMouseEnter={(e) => {
                   e.target.style.borderColor = '#3b82f6';
                   e.target.style.boxShadow = '0 0 0 3px rgba(59, 130, 246, 0.1)';
                 }}
                 onMouseLeave={(e) => {
                   e.target.style.borderColor = selectedTechnology ? '#10b981' : '#e2e8f0';
                   e.target.style.boxShadow = 'none';
                 }}
               >
                                 <option value="">Wszystkie technologie</option>
                 <option value="ADMIN">Admin</option>
                 <option value="ANALYTICS">Analytics</option>
                 <option value="AI">AI</option>
                 <option value="C">C</option>
                 <option value="DEVOPS">DevOps</option>
                 <option value="GAME">Game</option>
                 <option value="HTML">HTML</option>
                 <option value="JAVASCRIPT">JavaScript</option>
                 <option value="JAVA">Java</option>
                 <option value="MOBILE">Mobile</option>
                 <option value="NET">.NET</option>
                 <option value="PHP">PHP</option>
                 <option value="PM">PM</option>
                 <option value="PYTHON">Python</option>
                 <option value="RUBY">Ruby</option>
                 <option value="SCALA">Scala</option>
                 <option value="SECURITY">Security</option>
                 <option value="TESTING">Testing</option>
                 <option value="UX_UI">UX/UI</option>
              </select>
            </div>
            
            <div style={{
              display: 'flex',
              flexDirection: 'column',
              gap: '4px',
              '@media (max-width: 768px)': {
                gap: '6px'
              }
            }}>
              <label style={{
                fontSize: '11px',
                color: '#64748b',
                fontWeight: '500',
                textTransform: 'uppercase',
                letterSpacing: '0.5px'
              }}>
                Miasto
              </label>
                             <select 
                                   className="mobile-select"
                                   style={{
                    padding: '8px 12px',
                    border: `1px solid ${selectedCity ? '#10b981' : '#e2e8f0'}`,
                    borderRadius: '6px',
                    fontSize: '13px',
                    backgroundColor: 'white',
                    color: '#1e293b',
                    minWidth: '120px',
                    cursor: 'pointer',
                    transition: 'all 0.2s ease',
                    outline: 'none',
                    height: '36px'
                  }}
                 value={selectedCity}
                 onChange={(e) => setSelectedCity(e.target.value)}
                 onMouseEnter={(e) => {
                   e.target.style.borderColor = '#3b82f6';
                   e.target.style.boxShadow = '0 0 0 3px rgba(59, 130, 246, 0.1)';
                 }}
                 onMouseLeave={(e) => {
                   e.target.style.borderColor = selectedCity ? '#10b981' : '#e2e8f0';
                   e.target.style.boxShadow = 'none';
                 }}
               >
                                 <option value="">Wszystkie miasta</option>
                 <option value="GDANSK">Gdańsk</option>
                 <option value="KRAKOW">Kraków</option>
                 <option value="LODZ">Łódź</option>
                 <option value="POZNAN">Poznań</option>
                 <option value="SLASK">Śląsk</option>
                 <option value="WARSAW">Warszawa</option>
                 <option value="WROCLAW">Wrocław</option>
              </select>
            </div>
            
                                                                                                       <div 
                 className="mobile-badge"
                 style={{
                 color: '#64748b',
                 fontSize: '13px',
                 backgroundColor: '#f1f5f9',
                 padding: '6px 12px',
                 borderRadius: '8px',
                 fontWeight: '500',
                 textAlign: 'center'
               }}>
              Ostatnia aktualizacja: {lastUpdated}
            </div>
          </div>
        </div>

        {isLoading && (
          <div style={{
            textAlign: 'center',
            padding: '20px',
            color: '#64748b',
            fontSize: '14px',
            fontWeight: '500'
          }}>
            Ładowanie danych...
          </div>
        )}

        {error && (
          <div style={{
            textAlign: 'center',
            padding: '16px',
            color: '#dc2626',
            backgroundColor: '#fef2f2',
            borderRadius: '8px',
            fontSize: '14px',
            fontWeight: '500',
            border: '1px solid #fecaca'
          }}>
            {error}
          </div>
        )}

                                   {!isLoading && !error && (
                                                                                                               <div 
                  className="mobile-chart"
                  style={{ 
                  gridArea: 'chart',
                  display: 'flex',
                  flexDirection: 'column',
                  minHeight: 0,
                  maxHeight: 'calc(100vh - 200px)',
                  overflow: 'auto',
                  marginTop: '16px'
                }}>
                           <div style={{ 
                 height: '100%',
                 minHeight: 0,
                 maxHeight: '100%',
                 position: 'relative',
                 overflow: 'hidden'
               }}>
                 <CustomChart
                 options={{
                   ...chartOptions,
                                                      chart: {
                    ...chartOptions.chart,
                    height: window.innerWidth <= 768 ? 350 : 500,
                    width: '100%',
                    dropShadow: {
                      enabled: false
                    }
                  },
                                                   title: {
                    text: 'Oferty według poziomu doświadczenia (kliknij w dany poziom aby odfiltrować 😉)',
                    align: 'left',
                    margin: 20,
                    offsetY: 20,
                    offsetX: 0,
                    style: {
                      fontSize: '16px',
                      fontWeight: '600',
                      color: '#1e293b',
                      fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif"
                    }
                  },
                                 colors: ['#FF6B6B', '#4ECDC4', '#45B7D1', '#8B5CF6', '#F59E0B'],
                                 legend: {
                   position: 'top',
                   horizontalAlign: 'left',
                   fontSize: '13px',
                   fontWeight: '500',
                   fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif",
                   markers: {
                     width: 10,
                     height: 10,
                     radius: 5
                   }
                 },
                                 xaxis: {
                   ...chartOptions.xaxis,
                   type: 'datetime'
                 },
                                   tooltip: {
                    custom: function({ series, seriesIndex, dataPointIndex, w }) {
                      const date = new Date(w.globals.seriesX[seriesIndex][dataPointIndex]);
                      const day = date.getDate().toString().padStart(2, '0');
                      const month = (date.getMonth() + 1).toString().padStart(2, '0');
                      const year = date.getFullYear();
                      
                      let tooltip = `<div class="custom-tooltip" style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; font-size: 13px; line-height: 1.4;">`;
                      tooltip += `<div style="font-weight: 600; font-size: 14px; margin-bottom: 12px; color: #1e293b; letter-spacing: -0.025em;">${day}/${month}/${year}</div>`;
                      
                                             series.forEach((s, i) => {
                         const value = s[dataPointIndex];
                         // Skip if value is undefined (series is hidden)
                         if (value === undefined) return;
                         
                         const color = w.globals.colors[i];
                         tooltip += `<div style="display: flex; align-items: center; margin: 6px 0; font-weight: 500;">`;
                         tooltip += `<span style="width: 8px; height: 8px; border-radius: 50%; background-color: ${color}; margin-right: 10px; flex-shrink: 0;"></span>`;
                         tooltip += `<span style="color: #475569;">${w.globals.seriesNames[i]}: <strong style="color: #1e293b;">${value.toLocaleString()}</strong></span>`;
                         tooltip += `</div>`;
                       });
                      
                      tooltip += `</div>`;
                      return tooltip;
                    }
                  }
              }}
              series={[
                {
                  name: 'Wszystkie oferty',
                  data: offerData[0]?.data || []
                },
                ...levelData
              ]}
                             title="Wszystkie oferty według poziomu doświadczenia"
                          />
             </div>
           </div>
         )}
         
                   {/* Footer */}
                                           <div 
             className="mobile-footer"
             style={{
              textAlign: 'center',
              padding: '12px 0',
              borderTop: '1px solid #e2e8f0',
              backgroundColor: 'white',
              borderRadius: '0 0 16px 16px',
              gridArea: 'footer'
            }}>
           <div style={{
             fontSize: '13px',
             color: '#64748b',
             fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif"
           }}>
                           Created by{' '}
              <a 
                href="https://github.com/pawelprimus" 
                target="_blank" 
                rel="noopener noreferrer"
                style={{
                  color: '#3b82f6',
                  textDecoration: 'none',
                  fontWeight: '500',
                  transition: 'color 0.2s ease',
                  display: 'inline-flex',
                  alignItems: 'center'
                }}
                onMouseEnter={(e) => e.target.style.color = '#1d4ed8'}
                onMouseLeave={(e) => e.target.style.color = '#3b82f6'}
              >
                <svg 
                  width="16" 
                  height="16" 
                  viewBox="0 0 24 24" 
                  fill="currentColor"
                  style={{ marginRight: '4px' }}
                >
                  <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
                </svg>
                pawelprimus
              </a>
             , hosted on{' '}
             <a 
               href="https://mikr.us/?r=c12f3d8a" 
               target="_blank" 
               rel="noopener noreferrer"
               style={{
                 color: '#3b82f6',
                 textDecoration: 'none',
                 fontWeight: '500',
                 transition: 'color 0.2s ease'
               }}
               onMouseEnter={(e) => e.target.style.color = '#1d4ed8'}
               onMouseLeave={(e) => e.target.style.color = '#1d4ed8'}
             >
               Mikrus
             </a>
           </div>
         </div>
       </div>
     </div>
   );
 }

export default App;
