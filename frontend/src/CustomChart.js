import React from 'react';
import ReactApexChart from 'react-apexcharts';

const CustomChart = ({ options, series, title }) => {
  return (
    <div>
      <ReactApexChart options={options} series={series} type="line" height={700} />
    </div>
  );
};

export default CustomChart; 