import { Routes, Route } from 'react-router-dom'
import ProtectedRoute from './auth/ProtectedRoute'

import CustomerLayout from './layouts/CustomerLayout'
import OwnerLayout from './layouts/OwnerLayout'
import DeliveryLayout from './layouts/DeliveryLayout'
import AdminLayout from './layouts/AdminLayout'

import Login from './pages/Login'
import Register from './pages/Register'
import NotFound from './pages/NotFound'
import Unauthorized from './pages/Unauthorized'

import RestaurantList from './features/customer/RestaurantList'
import RestaurantDetail from './features/customer/RestaurantDetail'
import Cart from './features/customer/Cart'
import OrderHistory from './features/customer/OrderHistory'
import OrderDetail from './features/customer/OrderDetail'
import Profile from './features/customer/Profile'

import MyRestaurants from './features/owner/MyRestaurants'
import MenuManager from './features/owner/MenuManager'
import IncomingOrders from './features/owner/IncomingOrders'

import AvailableDeliveries from './features/delivery/AvailableDeliveries'
import AssignedOrders from './features/delivery/AssignedOrders'
import DeliveryHistory from './features/delivery/DeliveryHistory'

import Dashboard from './features/admin/Dashboard'
import ApproveRestaurants from './features/admin/ApproveRestaurants'
import ManageUsers from './features/admin/ManageUsers'
import ManageOrders from './features/admin/ManageOrders'

export default function App() {
  return (
    <Routes>
      {/* Public auth pages */}
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/unauthorized" element={<Unauthorized />} />

      {/* Order detail is reachable by whichever role the backend authorizes
          (customer/owner/delivery/admin), so it isn't nested in one role's layout. */}
      <Route element={<ProtectedRoute />}>
        <Route path="/orders/:id" element={<OrderDetail />} />
      </Route>

      {/* Restaurant browsing mirrors the backend: public, no login required */}
      <Route element={<CustomerLayout />}>
        <Route path="/" element={<RestaurantList />} />
        <Route path="/restaurants/:id" element={<RestaurantDetail />} />
      </Route>

      {/* Customer-only actions */}
      <Route element={<ProtectedRoute allowedRoles={['CUSTOMER']} />}>
        <Route element={<CustomerLayout />}>
          <Route path="/cart" element={<Cart />} />
          <Route path="/my-orders" element={<OrderHistory />} />
          <Route path="/profile" element={<Profile />} />
        </Route>
      </Route>

      {/* Restaurant owner */}
      <Route element={<ProtectedRoute allowedRoles={['RESTAURANT_OWNER']} />}>
        <Route element={<OwnerLayout />}>
          <Route path="/owner/restaurants" element={<MyRestaurants />} />
          <Route path="/owner/restaurants/:restaurantId/menu" element={<MenuManager />} />
          <Route path="/owner/orders" element={<IncomingOrders />} />
        </Route>
      </Route>

      {/* Delivery partner */}
      <Route element={<ProtectedRoute allowedRoles={['DELIVERY_PARTNER']} />}>
        <Route element={<DeliveryLayout />}>
          <Route path="/delivery/available" element={<AvailableDeliveries />} />
          <Route path="/delivery/assigned" element={<AssignedOrders />} />
          <Route path="/delivery/history" element={<DeliveryHistory />} />
        </Route>
      </Route>

      {/* Admin */}
      <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
        <Route element={<AdminLayout />}>
          <Route path="/admin/dashboard" element={<Dashboard />} />
          <Route path="/admin/restaurants" element={<ApproveRestaurants />} />
          <Route path="/admin/users" element={<ManageUsers />} />
          <Route path="/admin/orders" element={<ManageOrders />} />
        </Route>
      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}
