// src/chartOptions.js
const chartOptions = {
    chart: {
      type: 'line',
      height: '100%',
      parentHeightOffset: 0,
      offsetY: 0,
      offsetX: 0,
      margin: [0, 0, 0, 0],
      spacing: [0, 0, 0, 0],
      redrawOnWindowResize: true,
      animations: {
        enabled: false
      },
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
      tickAmount: 12,
      tickPlacement: 'on',
      min: undefined,
      max: undefined,
      
      title: {
        text: '',
        offsetY: 0
      },
      axisBorder: {
        show: false
      },
      axisTicks: {
        show: false
      },
      labels: {
        formatter: (value) => {
          const dateValue = new Date(value);
          const polishMonths = [
            'Sty', 'Lut', 'Mar', 'Kwi', 'Maj', 'Cze',
            'Lip', 'Sie', 'Wrz', 'Paź', 'Lis', 'Gru'
          ];
          const monthIndex = dateValue.getMonth();
          return `${polishMonths[monthIndex]} ${dateValue.getFullYear()}`; 
        },
        rotate: -45,
        rotateAlways: false,
        maxHeight: 40,
        offsetY: 0,
        style: {
          fontSize: '11px'
        }
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
        format: 'dd/MM/yyyy'
      },
      y: {
        formatter: function(value) {
          return value + ' ofert';
        }
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
  
  
  export default chartOptions;