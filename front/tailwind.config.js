/** @type {import('tailwindcss').Config} */
export default {
	content: ['./src/**/*.{html,js,svelte,ts}'],
	theme: {
		extend: {
			colors: {
				green1: '#90ee90',
				green2: '#65b67b',
				green3: '#78db4a',
				green4: '#64ca4f',
				green5: '#15b12a',
				green6: '#3bbe2a',
				green_background: '#fcfffd',
				yellow1: '#F9D537',
				yellow2: '#ffe02f',
				yellow3: '#f3db50',
				yellow4: '#e4e020',
				yellow5: '#ecdd07'
			}
		}
	},
	plugins: [require('daisyui')]
};
