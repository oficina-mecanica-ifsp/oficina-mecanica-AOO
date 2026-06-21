/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#fff3ea',
          100: '#ffe2cc',
          700: '#e8590c',
          800: '#c2440a',
          900: '#1f1f1f',
        },
      },
    },
  },
  plugins: [],
}
