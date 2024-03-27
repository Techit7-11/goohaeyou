/** @type {import('tailwindcss').Config} */
export default {
	content: ['./src/**/*.{html,js,svelte,ts}'],
	theme: {
		extend: {
			colors: {
				green1: '#90ee90',
				green2: '#65b67b',
				yellow1: '#F9D537'
			}
		}
	},
	plugins: [require('daisyui')]
};
