# Frontend - Digital Fixed Deposit System

## Overview
Modern Vue.js 3 application providing an intuitive user interface for digital fixed deposit banking operations. Built with Composition API, Pinia state management, and Tailwind CSS for responsive design.

## Technology Stack
- **Framework**: Vue.js 3.5.18 (Composition API)
- **Build Tool**: Vite 7.1.0
- **State Management**: Pinia 3.0.3
- **Routing**: Vue Router 4.5.1
- **Styling**: Tailwind CSS 3.4.17
- **UI Components**: Headless UI Vue 1.7.23
- **Icons**: Heroicons Vue 2.2.0
- **HTTP Client**: Axios 1.11.0
- **Form Validation**: VeeValidate 4.15.1 + Yup
- **Date Handling**: date-fns 4.1.0
- **Development**: PostCSS, Autoprefixer

## Project Structure

### 📁 Main Application
- **main.js**: Application entry point with Pinia and router setup
- **App.vue**: Root component with global layout structure
- **style.css**: Global styles and Tailwind CSS imports

### 📁 Pages (`pages/`)
Main application views representing different routes:

#### **Landing.vue**
- **Purpose**: Marketing landing page for unauthenticated users
- **Features**: Hero section, feature highlights, call-to-action buttons
- **Navigation**: Login/Register buttons, product information
- **Design**: Full-screen video background, responsive layout

#### **Login.vue**
- **Purpose**: User authentication interface
- **Features**: Email/password form, validation, error handling
- **Integration**: Auth service integration, JWT token management
- **UX**: Form validation feedback, loading states, redirect logic

#### **Register.vue**
- **Purpose**: New user account creation
- **Features**: Multi-field registration form, password confirmation
- **Validation**: Email format, password strength, required fields
- **Flow**: Account creation → automatic login → dashboard redirect

#### **Home.vue**
- **Purpose**: Main dashboard for authenticated users
- **Features**: FD overview, quick actions, recent transactions
- **Components**: Dashboard widgets, FD summary cards, action buttons
- **Data**: Real-time FD data, interest calculations, status updates

#### **Profile.vue**
- **Purpose**: User account management and settings
- **Features**: Profile information display, account details
- **Security**: Protected route, user data management
- **UI**: Clean profile layout, editable fields (future enhancement)

### 📁 Components (`components/`)
Reusable UI components organized by functionality:

#### **Navigation Components**
- **NavBar.vue**: Main navigation with user menu, logout, profile access
- **TabGroup.vue**: Reusable tab interface for content organization

#### **Fixed Deposit Components**
- **FixedDepositDashboard.vue**: Main FD overview with statistics and charts
- **FixedDepositTable.vue**: Tabular display of user's fixed deposits
- **FixedDepositTableRow.vue**: Individual FD row with actions and status
- **FDOverviewItem.vue**: Summary card showing FD statistics
- **FDSectionCard.vue**: Container component for FD sections
- **FDProgressBar.vue**: Visual progress indicator for FD maturity

#### **FD Creation Components**
- **FDCreateModal.vue**: Modal container for FD creation workflow
- **FDCreateStepForm.vue**: Multi-step form for FD parameters input
- **FDCreateStepReview.vue**: Review step before FD confirmation
- **CreateButton.vue**: Floating action button to initiate FD creation

#### **FD Management Components**
- **FDDetailsModal.vue**: Detailed view of individual FD with full information
- **BreakFixedDeposit.vue**: FD premature closure interface
- **BreakConfirmModal.vue**: Confirmation dialog for FD break operations

#### **Support System Components**
- **SupportPage.vue**: Main support interface with ticket management
- **SupportButton.vue**: Floating support access button
- **SupportForm.vue**: New support ticket creation form
- **SupportMenuModal.vue**: Support options and quick actions
- **SupportTicketModal.vue**: Individual ticket view and management
- **TicketList.vue**: List of user's support tickets
- **TicketChat.vue**: Chat interface for ticket communication
- **AdminTicketList.vue**: Admin view for all support tickets
- **AdminTicketChat.vue**: Admin interface for ticket responses

#### **UI Components**
- **CustomDropdown.vue**: Reusable dropdown with custom styling
- **StatusBadge.vue**: Status indicator with color coding
- **KebabMenu.vue**: Three-dot menu for actions
- **Pagination.vue**: Table pagination with page controls
- **UserProfileModal.vue**: User profile display modal

### 📁 Services (`services/`)
API integration layer handling HTTP requests:

#### **authService.js**
Authentication and user management:
```javascript
// Core Functions:
- login(credentials) // User authentication
- register(userData) // Account creation  
- getProfile() // User profile retrieval
- logout() // Session termination
- refreshToken() // Token renewal
```

**Features:**
- JWT token management
- Automatic token refresh
- Request/response interceptors
- Error handling and retry logic

#### **fdService.js**
Fixed deposit operations:
```javascript
// Core Functions:
- createFD(fdData) // Create new fixed deposit
- getUserFDs() // Get user's fixed deposits
- getActiveFDs() // Get active fixed deposits only
- getFDDetails(fdId) // Get detailed FD information
- calculateInterest(fdId) // Get accrued interest
- previewBreak(fdId) // Preview break calculation
- executeBreak(fdId, breakData) // Execute FD break
- updateFDStatus(fdId, status) // Update FD status
```

**Features:**
- Async/await pattern
- Error handling with user-friendly messages
- Request validation
- Response data transformation

#### **supportService.js**
Customer support system:
```javascript
// Core Functions:
- createTicket(ticketData) // Create support ticket
- getUserTickets() // Get user's tickets
- getTicketDetails(ticketId) // Get ticket information
- addMessage(ticketId, message) // Add message to ticket
- getTicketMessages(ticketId) // Get ticket conversation
- updateTicketStatus(ticketId, status) // Update ticket status
```

**Features:**
- Real-time message handling
- File attachment support (future)
- Ticket status management
- Message threading

### 📁 Stores (`stores/`)
Pinia state management:

#### **auth.js**
Global authentication state:
```javascript
// State:
- user: null // Current user information
- token: null // JWT authentication token
- isLoading: false // Loading state indicator

// Getters:
- isAuthenticated() // Check if user is logged in
- userRole() // Get user role/permissions
- userName() // Get display name

// Actions:
- login(credentials) // Authenticate user
- register(userData) // Create new account
- logout() // Clear session
- loadUserFromStorage() // Restore session
- updateProfile(profileData) // Update user info
```

**Features:**
- Persistent storage (localStorage)
- Automatic session restoration
- Token expiration handling
- User state synchronization

### 📁 Router (`router/`)

#### **index.js**
Vue Router configuration with route guards:

**Route Structure:**
```javascript
- / (root) // Smart redirect based on auth status
- /landing // Marketing page for unauthenticated users
- /login // Authentication page
- /register // Account creation page
- /home // Main dashboard (protected)
- /profile // User profile (protected)
- /support // Support system (protected)
```

**Route Guards:**
- **Authentication Guard**: Redirects unauthenticated users to login
- **Smart Root Redirect**: Sends users to appropriate page based on auth status
- **Protected Routes**: Ensures only authenticated users access sensitive pages

### 📁 Assets (`assets/`)
Static resources and media files:

**Images:**
- **logo.png**: Application branding
- **landing.png**: Landing page hero image
- **home.png**: Dashboard illustrations
- **profile.png**: Profile page graphics
- **support.png**: Support system icons
- **message.png**: Chat/messaging icons
- **warning.png/warning2.png**: Alert and warning indicators
- **arrowDown.png**: UI navigation elements

**Media:**
- **bg.mp4**: Background video for landing page

**Icons:**
- **favicon.png**: Browser tab icon
- **vite.svg**: Development framework logo
- **vue.svg**: Vue.js framework logo

## Key Features

### 🔐 Authentication & Security
- JWT-based authentication with automatic refresh
- Protected routes with navigation guards
- Persistent session management
- Secure token storage and handling

### 💰 Fixed Deposit Management
- **Creation Workflow**: Multi-step FD creation with validation
- **Dashboard Overview**: Visual FD portfolio with statistics
- **Interest Calculation**: Real-time interest accrual display
- **Break Management**: Premature closure with penalty calculation
- **Status Tracking**: Visual status indicators and progress bars

### 🎨 User Experience
- **Responsive Design**: Mobile-first approach with Tailwind CSS
- **Loading States**: Smooth loading indicators and skeleton screens
- **Error Handling**: User-friendly error messages and recovery options
- **Form Validation**: Real-time validation with clear feedback
- **Modal Interfaces**: Clean modal dialogs for complex operations

### 🎯 Support System
- **Ticket Management**: Create and track support requests
- **Real-time Chat**: Message threading with support agents
- **Status Updates**: Visual ticket status progression
- **Quick Actions**: Common support tasks and FAQs

## Component Architecture

### 🏗️ Composition API Pattern
```vue
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

// Reactive state
const isLoading = ref(false)
const fdData = ref([])

// Computed properties
const activeFDs = computed(() => 
  fdData.value.filter(fd => fd.status === 'ACTIVE')
)

// Lifecycle hooks
onMounted(async () => {
  await loadFDData()
})

// Methods
const loadFDData = async () => {
  isLoading.value = true
  try {
    fdData.value = await fdService.getUserFDs()
  } catch (error) {
    handleError(error)
  } finally {
    isLoading.value = false
  }
}
</script>
```

### 🎨 Styling Strategy
- **Tailwind CSS**: Utility-first CSS framework
- **Component Scoping**: Scoped styles for component isolation
- **Design System**: Consistent color palette and spacing
- **Responsive Design**: Mobile-first breakpoint system

### 📱 Responsive Design
```css
/* Mobile First Approach */
.fd-card {
  @apply p-4 rounded-lg shadow-md;
}

/* Tablet */
@screen md {
  .fd-card {
    @apply p-6;
  }
}

/* Desktop */
@screen lg {
  .fd-card {
    @apply p-8 shadow-lg;
  }
}
```

## State Management

### 🗄️ Pinia Store Pattern
```javascript
export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref(null)
  const token = ref(localStorage.getItem('token'))
  
  // Getters
  const isAuthenticated = computed(() => !!token.value)
  
  // Actions
  const login = async (credentials) => {
    const response = await authService.login(credentials)
    token.value = response.token
    user.value = response.user
    localStorage.setItem('token', response.token)
  }
  
  return { user, token, isAuthenticated, login }
})
```

## API Integration

### 🌐 HTTP Client Configuration
```javascript
// Axios instance with interceptors
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

// Request interceptor for auth token
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor for error handling
apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Handle token expiration
      authStore.logout()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)
```

## Configuration Files

### **vite.config.js**
```javascript
export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0', // Docker compatibility
    port: 5173
  },
  build: {
    outDir: 'dist',
    sourcemap: true
  }
})
```

### **tailwind.config.js**
```javascript
module.exports = {
  content: ['./index.html', './src/**/*.{vue,js,ts}'],
  theme: {
    extend: {
      colors: {
        primary: '#3B82F6',
        secondary: '#10B981'
      }
    }
  },
  plugins: []
}
```

## Performance Optimization

### 🚀 Code Splitting
- Route-based code splitting with Vue Router
- Component lazy loading for large components
- Dynamic imports for heavy libraries

### 📦 Bundle Optimization
- Tree shaking for unused code elimination
- Asset optimization with Vite
- Gzip compression for production builds

### 🎯 Runtime Performance
- Virtual scrolling for large lists
- Debounced search inputs
- Memoized computed properties
- Efficient re-rendering with key attributes

## Security Considerations

### 🔒 Client-Side Security
- XSS prevention with Vue's built-in sanitization
- CSRF protection with token validation
- Secure token storage practices
- Input validation and sanitization

### 🛡️ Authentication Security
- JWT token expiration handling
- Automatic logout on token expiry
- Secure HTTP-only cookie options (future enhancement)
- Route-based access control

## Browser Compatibility

### 📱 Supported Browsers
- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

### 🔧 Polyfills
- ES6+ features with Vite's built-in polyfills
- CSS Grid and Flexbox support
- Modern JavaScript API compatibility

## Deployment

### 🐳 Docker Deployment
```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build
EXPOSE 5173
CMD ["npm", "run", "preview"]
```

### 🌐 Production Build
```bash
npm run build
# Generates optimized static files in dist/
```

## Future Enhancements

### 🔮 Planned Features
- **Progressive Web App (PWA)**: Offline support and app-like experience
- **Real-time Notifications**: WebSocket integration for live updates
- **Advanced Charts**: Interactive FD performance visualizations
- **Mobile App**: React Native or Flutter mobile application
- **Accessibility**: WCAG 2.1 AA compliance improvements
- **Internationalization**: Multi-language support with Vue I18n