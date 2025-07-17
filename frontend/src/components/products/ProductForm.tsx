// Formulario de productos
import { useState } from 'react';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import { Card } from '../ui/Card';
import { Upload, X } from 'lucide-react';

interface ProductFormProps {
  product?: any;
  onSubmit: (data: any) => void;
  onCancel: () => void;
}

export function ProductForm({ product, onSubmit, onCancel }: ProductFormProps) {
  const [formData, setFormData] = useState({
    name: product?.name || '',
    sku: product?.sku || '',
    description: product?.description || '',
    category: product?.category || '',
    brand: product?.brand || '',
    price: product?.price || '',
    cost: product?.cost || '',
    stock: product?.stock || '',
    minStock: product?.minStock || '',
    sizes: product?.sizes || [],
    colors: product?.colors || [],
    isActive: product?.isActive ?? true,
  });

  const [images, setImages] = useState<string[]>(product?.images || []);

  const categories = [
    'Blusas', 'Pantalones', 'Vestidos', 'Camisas', 'Faldas',
    'Chaquetas', 'Shorts', 'Jeans', 'Tops', 'Accesorios'
  ];

  const availableSizes = ['XS', 'S', 'M', 'L', 'XL', 'XXL'];
  const availableColors = [
    { name: 'Negro', hex: '#000000' },
    { name: 'Blanco', hex: '#FFFFFF' },
    { name: 'Azul', hex: '#3B82F6' },
    category: product?.category?.id ? { id: product.category.id } : { id: '' },
    { name: 'Verde', hex: '#10B981' },
    { name: 'Rosa', hex: '#EC4899' },
    { name: 'Amarillo', hex: '#F59E0B' },
    { name: 'Gris', hex: '#6B7280' },
  ];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({ ...formData, images });
  };

  const handleSizeToggle = (size: string) => {
  // Debes obtener las categorías con id y nombre desde el backend, aquí ejemplo:
  const categories = [
    { id: 1, nombre: 'Ropa Femenina' },
    { id: 2, nombre: 'Ropa Masculina' },
    { id: 3, nombre: 'Accesorios' },
    { id: 4, nombre: 'Calzado' },
    { id: 5, nombre: 'Blusas' },
    { id: 6, nombre: 'Vestidos' },
    { id: 7, nombre: 'Pantalones Femeninos' },
    { id: 8, nombre: 'Faldas' },
    { id: 9, nombre: 'Camisas Masculinas' },
    { id: 10, nombre: 'Pantalones Masculinos' },
    { id: 11, nombre: 'Polos' },
    { id: 12, nombre: 'Bolsos' },
    { id: 13, nombre: 'Cinturones' },
    { id: 14, nombre: 'Joyas' },
    { id: 15, nombre: 'Zapatos Formales' },
    { id: 16, nombre: 'Zapatillas' },
    { id: 17, nombre: 'Sandalias' },
  ];
  };

  const handleColorToggle = (color: string) => {
    // Enviar la categoría como objeto { id: ... }
    onSubmit({
      ...formData,
      category: { id: formData.category.id },
      images
    });
      ? formData.colors.filter((c: string) => c !== color)
      : [...formData.colors, color];
    setFormData({ ...formData, colors: newColors });
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Información Básica */}
        <Card className="p-4">
          <h3 className="font-semibold text-gray-900 mb-4">Información Básica</h3>
          <div className="space-y-4">
            <Input
              label="Nombre del Producto"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
            />
            
            <Input
              label="SKU"
              value={formData.sku}
              onChange={(e) => setFormData({ ...formData, sku: e.target.value })}
              required
            />
            
            <div>
              <select
                value={formData.category.id}
                onChange={(e) => setFormData({ ...formData, category: { id: e.target.value } })}
                className="block w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-yellow-500 focus:border-yellow-500"
                required
              >
                <option value="">Seleccionar categoría</option>
                {categories.map((cat) => (
                  <option key={cat.id} value={cat.id}>{cat.nombre}</option>
                ))}
              </select>
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>
            </div>
            
            <Input
              label="Marca"
              value={formData.brand}
              onChange={(e) => setFormData({ ...formData, brand: e.target.value })}
            />
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Descripción
              </label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                rows={3}
                className="block w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-yellow-500 focus:border-yellow-500"
                placeholder="Descripción del producto..."
              />
            </div>
          </div>
        </Card>

        {/* Precios e Inventario */}
        <Card className="p-4">
          <h3 className="font-semibold text-gray-900 mb-4">Precios e Inventario</h3>
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Precio de Venta (S/)"
                type="number"
                step="0.01"
                value={formData.price}
                onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                required
              />
              
              <Input
                label="Costo (S/)"
                type="number"
                step="0.01"
                value={formData.cost}
                onChange={(e) => setFormData({ ...formData, cost: e.target.value })}
                required
              />
            </div>
            
            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Stock Actual"
                type="number"
                value={formData.stock}
                onChange={(e) => setFormData({ ...formData, stock: e.target.value })}
                required
              />
              
              <Input
                label="Stock Mínimo"
                type="number"
                value={formData.minStock}
                onChange={(e) => setFormData({ ...formData, minStock: e.target.value })}
                required
              />
            </div>
            
            {formData.price && formData.cost && (
              <div className="bg-green-50 p-3 rounded-lg">
                <p className="text-sm text-green-800">
                  <span className="font-medium">Margen:</span> S/ {(parseFloat(formData.price) - parseFloat(formData.cost)).toFixed(2)} 
                  ({(((parseFloat(formData.price) - parseFloat(formData.cost)) / parseFloat(formData.price)) * 100).toFixed(1)}%)
                </p>
              </div>
            )}
          </div>
        </Card>
      </div>

      {/* Tallas */}
      <Card className="p-4">
        <h3 className="font-semibold text-gray-900 mb-4">Tallas Disponibles</h3>
        <div className="flex flex-wrap gap-2">
          {availableSizes.map((size) => (
            <button
              key={size}
              type="button"
              onClick={() => handleSizeToggle(size)}
              className={`px-3 py-2 rounded-lg border-2 transition-colors ${
                formData.sizes.includes(size)
                  ? 'border-yellow-500 bg-yellow-100 text-yellow-800'
                  : 'border-gray-300 hover:border-gray-400'
              }`}
            >
              {size}
            </button>
          ))}
        </div>
      </Card>

      {/* Colores */}
      <Card className="p-4">
        <h3 className="font-semibold text-gray-900 mb-4">Colores Disponibles</h3>
        <div className="grid grid-cols-4 gap-3">
          {availableColors.map((color) => (
            <button
              key={color.name}
              type="button"
              onClick={() => handleColorToggle(color.name)}
              className={`flex items-center space-x-2 p-2 rounded-lg border-2 transition-colors ${
                formData.colors.includes(color.name)
                  ? 'border-yellow-500 bg-yellow-50'
                  : 'border-gray-300 hover:border-gray-400'
              }`}
            >
              <div
                className="w-6 h-6 rounded-full border-2 border-gray-300"
                style={{ backgroundColor: color.hex }}
              />
              <span className="text-sm">{color.name}</span>
            </button>
          ))}
        </div>
      </Card>

      {/* Imágenes */}
      <Card className="p-4">
        <h3 className="font-semibold text-gray-900 mb-4">Imágenes del Producto</h3>
        <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
          <Upload className="w-8 h-8 text-gray-400 mx-auto mb-2" />
          <p className="text-gray-500">Arrastra y suelta imágenes aquí, o haz clic para seleccionar</p>
          <p className="text-xs text-gray-400 mt-1">PNG, JPG hasta 5MB cada una</p>
        </div>
      </Card>

      {/* Botones */}
      <div className="flex justify-end space-x-3 pt-6 border-t">
        <Button variant="ghost" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit">
          {product ? 'Actualizar Producto' : 'Crear Producto'}
        </Button>
      </div>
    </form>
  );
}