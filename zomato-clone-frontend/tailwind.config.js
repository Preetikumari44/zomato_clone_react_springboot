/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#14181F',
        base: '#F7F8FA',
        marigold: {
          DEFAULT: '#F5A524',
          dark: '#D6890F',
        },
        chili: '#E4572E',
        basil: '#2F9E44',
        line: '#E4E6EB',
      },
      fontFamily: {
        display: ['"Fraunces"', 'Georgia', 'serif'],
        body: ['"Inter"', 'system-ui', 'sans-serif'],
        mono: ['"IBM Plex Mono"', 'ui-monospace', 'monospace'],
      },
      backgroundImage: {
        perforation: 'radial-gradient(circle, #E4E6EB 1.5px, transparent 1.5px)',
      },
      backgroundSize: {
        perforation: '10px 10px',
      },
    },
  },
  plugins: [],
}
