# Eldorado Job Market Dashboard

A React-based dashboard application that monitors job offer trends and analyzes the job market across different experience levels.

## Project Description

This dashboard visualizes job offer data over time, specifically tracking opportunities across different seniority levels (C_LEVEL, SENIOR, MID, JUNIOR). The application monitors whether "eldorado" (a company/platform) is experiencing changes in job market activity.

### Features
- Real-time job offer data visualization
- Experience level breakdown (C_LEVEL, SENIOR, MID, JUNIOR)
- Time-series charts using ApexCharts
- Last updated timestamp
- Responsive Material-UI design

### Tech Stack
- React 18 with functional components and hooks
- Material-UI for styling
- ApexCharts for data visualization
- Axios for API communication
- Webpack for bundling

## Development Rules & Guidelines

### Code Style & Formatting
- Use functional components with hooks (React 18+)
- Prefer `const` over `let` when possible
- Use arrow functions for components
- Use destructuring for props and state
- Use template literals instead of string concatenation
- Use optional chaining (`?.`) and nullish coalescing (`??`) operators

### Naming Conventions
- Use `camelCase` for variables, functions, and props
- Use `PascalCase` for components and classes
- Use `UPPER_SNAKE_CASE` for constants
- Use descriptive names (avoid abbreviations)

### File Organization
- One component per file
- Use `index.js` for barrel exports
- Group related components in folders
- Keep utility functions in separate files

### Comments & Documentation
- Write JSDoc comments for complex functions
- Use inline comments sparingly, prefer self-documenting code
- Add TODO comments for future improvements

### Error Handling
- Always use try-catch for async operations
- Provide meaningful error messages
- Use error boundaries for React components
- Log errors to console for debugging

### Performance
- Use `React.memo` for expensive components
- Use `useMemo` and `useCallback` when appropriate
- Avoid inline object/function creation in render
- Use lazy loading for large components

### Testing Preferences
- Write unit tests for utility functions
- Use React Testing Library for component tests
- Mock external dependencies
- Test error scenarios

### Git Workflow
- Use conventional commit messages
- Create feature branches for new features
- Keep commits atomic and focused
- Write descriptive commit messages

## Getting Started

1. Install dependencies:
   ```bash
   npm install
   ```

2. Set up environment variables:
   ```bash
   REACT_APP_API_URL=http://localhost:8080/api
   ```

3. Start development server:
   ```bash
   npm start
   ```

## API Endpoints

- `GET /api/trigger/newest` - Get last update timestamp
- `GET /api/levels` - Get job offer data by experience level

## Project Structure

```
src/
├── App.js          # Main application component
├── CustomChart.js  # Chart wrapper component
├── chartOptions.js # Chart configuration
├── config.js       # API configuration
└── index.js        # Application entry point
```
