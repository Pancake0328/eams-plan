# Enterprise Asset Management System (EAMS) - New Frontend

This is the modernized frontend for the EAMS project, built with Vue 3, TypeScript, Vite, and Element Plus.

## Features

- **Modern UI/UX**: Clean, responsive design using Element Plus.
- **Type Safety**: Full TypeScript support for better maintainability.
- **Performance**: Powered by Vite for fast development and building.
- **State Management**: Using Pinia for predictable state updates.

## Project Setup

### Install Dependencies

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Type-Check, Compile and Minify for Production

```sh
npm run build
```

## Directory Structure

- `src/api`: API integration modules.
- `src/layout`: Main application layout (Sidebar, Header).
- `src/router`: Routing configuration.
- `src/stores`: Pinia state stores.
- `src/types`: TypeScript type definitions.
- `src/utils`: Utility functions (Axios wrapper, etc.).
- `src/views`: Page components.

## Deployment

The built files are located in the `dist` directory. These static files can be served by any web server (Nginx, Apache, etc.).
