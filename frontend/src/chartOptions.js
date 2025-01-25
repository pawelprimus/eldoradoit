// src/chartOptions.js
const chartOptions = {
    chart: {
      type: 'line',
      height: 700,
      zoom: {
        enabled: true,
        type: 'x',
        autoScaleYaxis: true,
        zoomedArea: {
          fill: {
            color: '#90CAF9',
            opacity: 0.4
          },
          stroke: {
            color: '#0D47A1',
            opacity: 0.4,
            width: 1
          }
        }
      },
      toolbar: {
        autoSelected: 'zoom'
      }
    },
    stroke: {
      curve: 'smooth',
      width: 2
    },
    grid: {
      borderColor: '#e7e7e7',
      row: {
        colors: ['#f3f3f3', 'transparent'],
        opacity: 0.5
      }
    },
    markers: {
      size: 3
    },
    xaxis: {
      type: 'datetime',
      labels: {
        formatter: (value) => {
          const dateValue = new Date(value);
          const polishMonths = [
            'Sty', 'Lut', 'Mar', 'Kwi', 'Maj', 'Cze',
            'Lip', 'Sie', 'Wrz', 'Paź', 'Lis', 'Gru'
          ];
          const monthIndex = dateValue.getMonth();
          return `${polishMonths[monthIndex]} ${dateValue.getFullYear()}`; 
        }
      },
      title: {
        text: 'Data'
      }
    },
    yaxis: {
      title: {
        text: 'Liczba ofert'
      },
      min: undefined,
      max: undefined
    },
    tooltip: {
      x: {
        format: 'dd MMM yyyy'
      }
    },
    title: {
      text: 'Liczba ofert w czasie',
      align: 'left',
      style: {
        fontSize: '20px'
      }
    },
    theme: {
      mode: 'light',
      palette: 'palette1'
    },
    colors: ['#008FFB', '#00E396', '#FEB019'],
    annotations: {
      xaxis: [{
        x: new Date('2024-01-01').getTime(),
        borderColor: '#775DD0',
        label: {
          text: 'Important Event'
        }
      }]
    },
    toolbar: {
      show: true,
      tools: {
        download: true,
        selection: true,
        zoom: true,
        zoomin: true,
        zoomout: true,
        pan: true,
        reset: true
      }
    }
  };
  
//   const polishMonths = [
//     'Sty', 'Lut', 'Mar', 'Kwi', 'Maj', 'Cze',
//     'Lip', 'Sie', 'Wrz', 'Paź', 'Lis', 'Gru'
//   ];
  
//   // Update the datetimeFormatter to use Polish month names
//   chartOptions.xaxis.labels.datetimeFormatter.month = (value) => {
//     console.log('Value passed to month formatter:', value); // Debugging line
//     // const monthIndex = new Date(value).getMonth();
//     return polishMonths[value];
//   };
  
  export default chartOptions;