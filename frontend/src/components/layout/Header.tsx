// Header principal con información del usuario
import { useState } from 'react';
import { Bell, Search, Menu, LogOut, User, Settings, Sun, Moon, Zap } from 'lucide-react';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';

interface HeaderProps {
  onToggleSidebar: () => void;
  onLogout: () => void;
  user: any;
}

export function Header({ onToggleSidebar, onLogout, user }: HeaderProps) {
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [notifications] = useState([
    { id: 1, message: 'Stock bajo en productos', type: 'warning', time: '5 min' },
    { id: 2, message: 'Nueva venta registrada', type: 'success', time: '10 min' },
  ]);

  const roleDisplayNames: Record<string, string> = {
    SUPER_ADMIN: 'Super Administrador',
    ADMIN: 'Administrador',
    MANAGER: 'Gerente',
    VENDEDOR: 'Vendedor',
    CAJERO: 'Cajero',
    INVENTARIO: 'Encargado de Inventario'
  };

  return (
    <header className="bg-white border-b border-gray-200 shadow-sm">
      <div className="px-6 py-4">
        <div className="flex items-center justify-between">
          {/* Left Section */}
          <div className="flex items-center space-x-6">
            <Button
              variant="ghost"
              size="sm"
              onClick={onToggleSidebar}
              className="lg:hidden hover:bg-gray-100"
            >
              <Menu className="w-5 h-5" />
            </Button>
            
            {/* Enhanced Search */}
            <div className="hidden md:block relative">
              <Input
                placeholder="Buscar productos, clientes, ventas..."
                leftIcon={<Search className="w-4 h-4 text-gray-400" />}
                className="w-96 bg-gray-50 border-gray-200 focus:bg-white focus:ring-2 focus:ring-yellow-500/20 focus:border-yellow-500 transition-all duration-200"
              />
              <div className="absolute right-3 top-1/2 transform -translate-y-1/2">
                <kbd className="px-2 py-1 text-xs bg-gray-200 rounded text-gray-500">⌘K</kbd>
              </div>
            </div>
          </div>

          {/* Right Section */}
          <div className="flex items-center space-x-4">
            {/* Quick Actions */}
            <div className="hidden lg:flex items-center space-x-2">
              <Button variant="ghost" size="sm" className="text-gray-600 hover:text-yellow-600">
                <Zap className="w-4 h-4 mr-1" />
                Nueva Venta
              </Button>
            </div>

            {/* Theme Toggle */}
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setIsDarkMode(!isDarkMode)}
              className="text-gray-600 hover:text-yellow-600"
            >
              {isDarkMode ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
            </Button>

            {/* Notifications */}
            <div className="relative">
              <Button variant="ghost" size="sm" className="relative text-gray-600 hover:text-yellow-600">
                <Bell className="w-5 h-5" />
                {notifications.length > 0 && (
                  <span className="absolute -top-1 -right-1 w-5 h-5 bg-gradient-to-r from-red-500 to-red-600 rounded-full text-xs flex items-center justify-center text-white font-bold animate-pulse">
                    {notifications.length}
                  </span>
                )}
              </Button>
            </div>

            {/* User Profile */}
            <div className="flex items-center space-x-3">
              <div className="hidden sm:block text-right">
                <p className="text-sm font-medium text-gray-900">
                  {user?.firstName} {user?.lastName}
                </p>
                <p className="text-xs text-gray-500">
                  {roleDisplayNames[user?.role] || user?.role}
                </p>
              </div>
              
              <div className="relative">
                <div className="w-10 h-10 bg-gradient-to-r from-yellow-400 via-yellow-500 to-yellow-600 rounded-full flex items-center justify-center shadow-lg ring-2 ring-yellow-200">
                  <User className="w-5 h-5 text-black" />
                </div>
                <div className="absolute -bottom-1 -right-1 w-4 h-4 bg-green-500 rounded-full border-2 border-white"></div>
              </div>
            </div>

            {/* Settings & Logout */}
            <div className="flex items-center space-x-1">
              <Button variant="ghost" size="sm" className="text-gray-600 hover:text-yellow-600">
                <Settings className="w-5 h-5" />
              </Button>
              
              <Button 
                variant="ghost" 
                size="sm" 
                onClick={onLogout}
                className="text-gray-600 hover:text-red-600"
              >
                <LogOut className="w-5 h-5" />
              </Button>
            </div>
          </div>
        </div>

        {/* Quick Stats Bar */}
        <div className="mt-4 flex items-center justify-between bg-gradient-to-r from-gray-50 to-gray-100 rounded-lg p-3">
          <div className="flex items-center space-x-6">
            <div className="flex items-center space-x-2">
              <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
              <span className="text-sm text-gray-600">Sistema Online</span>
            </div>
            <div className="text-sm text-gray-600">
              Usuario: <span className="font-bold text-yellow-600">{user?.firstName}</span>
            </div>
            <div className="text-sm text-gray-600">
              Rol: <span className="font-bold text-blue-600">{roleDisplayNames[user?.role] || user?.role}</span>
            </div>
          </div>
          
          <div className="text-xs text-gray-500">
            Última actualización: {new Date().toLocaleTimeString('es-PE')}
          </div>
        </div>
      </div>
    </header>
  );
}