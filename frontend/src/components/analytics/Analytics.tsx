import { useState, useEffect } from "react";
import {
  TrendingUp,
  Users,
  Package,
  DollarSign,
  Target,
  BarChart3,
  PieChart,
  Activity,
  Zap,
  Star,
} from "lucide-react";
import { Card, CardHeader, CardTitle, CardContent } from "../ui/Card";
import { Button } from "../ui/Button";
import { SalesChart } from "../charts/SalesChart";
import { CustomPieChart } from "../charts/PieChart";
import { CustomBarChart } from "../charts/BarChart";
import { AnalyticsService } from "../../services/analyticsService";

// Datos mock para evitar errores de variables no definidas
const mockCustomers = Array.from({ length: 150 }, (_, i) => ({
  id: i + 1,
  name: `Cliente ${i + 1}`,
}));
const mockProducts = Array.from({ length: 85 }, (_, i) => ({
  id: i + 1,
  name: `Producto ${i + 1}`,
}));
const mockTotalRevenue = 45000;

export function Analytics() {
  const [timeRange, setTimeRange] = useState("30d");
  const [activeMetric, setActiveMetric] = useState("revenue");
  const [analyticsData, setAnalyticsData] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAnalyticsData();
  }, [timeRange]);

  const loadAnalyticsData = async () => {
    setLoading(true);
    try {
      const [kpisResponse, segmentsResponse, trendsResponse] =
        await Promise.all([
          AnalyticsService.getKPIs(),
          AnalyticsService.getCustomerSegments(),
          AnalyticsService.getSalesTrends(parseInt(timeRange.replace("d", ""))),
        ]);

      setAnalyticsData({
        kpiData: kpisResponse.data.kpiData.map((kpi: any) => ({
          ...kpi,
        })),
        customerSegments: segmentsResponse.data.customerSegments,
        categoryData: [
          { name: "Ropa Femenina", value: 45, color: "#FFD700" },
          { name: "Ropa Masculina", value: 30, color: "#FFA500" },
          { name: "Accesorios", value: 15, color: "#FF8C00" },
          { name: "Calzado", value: 10, color: "#FF7F50" },
        ],
        channelData: [
          { name: "Online", value: 60, color: "#4F46E5" },
          { name: "Tienda Física", value: 40, color: "#10B981" },
        ],
        salesData: trendsResponse.data.salesData,
        productPerformance: [
          {
            category: "Ropa Femenina",
            sales: 45,
            revenue: 15000,
            margin: 45,
            trend: "up",
            emoji: "👚",
          },
          {
            category: "Ropa Masculina",
            sales: 30,
            revenue: 12000,
            margin: 40,
            trend: "up",
            emoji: "👔",
          },
          {
            category: "Accesorios",
            sales: 15,
            revenue: 5000,
            margin: 55,
            trend: "stable",
            emoji: "👜",
          },
          {
            category: "Calzado",
            sales: 10,
            revenue: 8000,
            margin: 35,
            trend: "up",
            emoji: "👠",
          },
        ],
      });
    } catch (error) {
      console.error("Error loading analytics data:", error);
    } finally {
      setLoading(false);
    }
  };

  const timeRanges = [
    { value: "7d", label: "7 días", emoji: "📅" },
    { value: "30d", label: "30 días", emoji: "📊" },
    { value: "90d", label: "3 meses", emoji: "📈" },
    { value: "1y", label: "1 año", emoji: "📆" },
  ];

  // Datos para gráficos avanzados
  const advancedSalesData = [
    { date: "2024-01-10", sales: 890, orders: 11, customers: 9 },
    { date: "2024-01-11", sales: 1100, orders: 14, customers: 12 },
    { date: "2024-01-12", sales: 1450, orders: 18, customers: 15 },
    { date: "2024-01-13", sales: 980, orders: 12, customers: 10 },
    { date: "2024-01-14", sales: 1250, orders: 15, customers: 13 },
    { date: "2024-01-15", sales: 1680, orders: 21, customers: 18 },
    { date: "2024-01-16", sales: 1320, orders: 16, customers: 14 },
  ];

  const hourlyData = [
    { name: "9:00", value: 45 },
    { name: "10:00", value: 78 },
    { name: "11:00", value: 123 },
    { name: "12:00", value: 156 },
    { name: "13:00", value: 89 },
    { name: "14:00", value: 134 },
    { name: "15:00", value: 167 },
    { name: "16:00", value: 198 },
    { name: "17:00", value: 145 },
    { name: "18:00", value: 112 },
  ];

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-yellow-500"></div>
        <span className="ml-3 text-gray-600">Cargando analytics...</span>
      </div>
    );
  }

  if (!analyticsData) {
    return (
      <div className="text-center py-12">
        <div className="text-red-500 mb-4">
          Error al cargar datos de analytics
        </div>
        <Button onClick={loadAnalyticsData}>Reintentar</Button>
      </div>
    );
  }
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-4xl font-bold bg-gradient-to-r from-purple-600 via-pink-600 to-blue-600 bg-clip-text text-transparent">
            🚀 Analytics Avanzado
          </h1>
          <p className="text-gray-600 mt-2">
            Inteligencia de negocio y métricas de rendimiento en tiempo real
          </p>
        </div>
        <div className="flex space-x-2">
          {timeRanges.map((range) => (
            <Button
              key={range.value}
              variant={timeRange === range.value ? "primary" : "ghost"}
              size="sm"
              onClick={() => setTimeRange(range.value)}
              className={
                timeRange === range.value
                  ? "bg-gradient-to-r from-purple-500 to-pink-500"
                  : ""
              }
            >
              {range.emoji} {range.label}
            </Button>
          ))}
        </div>
      </div>

      {/* KPIs Principales Mejorados */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {analyticsData.kpiData.map((kpi: any, index: number) => (
          <Card
            key={index}
            className="p-6 hover:shadow-2xl transition-all duration-300 bg-gradient-to-br from-white to-gray-50 border-l-4 border-l-purple-400 hover:scale-105"
          >
            <div className="flex items-center justify-between">
              <div className="flex-1">
                <div className="flex items-center space-x-2 mb-2">
                  <span className="text-2xl">{kpi.emoji}</span>
                  <p className="text-sm font-medium text-gray-600">
                    {kpi.title}
                  </p>
                </div>
                <p className="text-3xl font-bold text-gray-900 mb-2">
                  {kpi.value}
                </p>
                <p
                  className={`text-sm flex items-center font-medium ${
                    kpi.trend === "up" ? "text-green-600" : "text-red-600"
                  }`}
                >
                  <span className="mr-1 text-lg">
                    {kpi.trend === "up" ? "📈" : "📉"}
                  </span>
                  {kpi.change}
                </p>
              </div>
              <div
                className={`w-16 h-16 rounded-2xl flex items-center justify-center bg-gradient-to-br from-${kpi.color}-100 to-${kpi.color}-200 shadow-lg`}
              >
                <span className="text-3xl">{kpi.emoji}</span>
              </div>
            </div>
          </Card>
        ))}
      </div>

      {/* Gráficos Principales */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Tendencia de Ventas Avanzada */}
        <Card className="bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 border border-blue-200">
          <CardHeader>
            <CardTitle className="flex items-center text-blue-800">
              <>
                <TrendingUp className="w-6 h-6 mr-2" />
                <span>📊 Tendencia de Ventas Avanzada</span>
              </>
            </CardTitle>
          </CardHeader>
          <CardContent>
            <SalesChart data={advancedSalesData} type="area" />
          </CardContent>
        </Card>

        {/* Canales de Venta */}
        <Card className="bg-gradient-to-br from-green-50 via-emerald-50 to-teal-50 border border-green-200">
          <CardHeader>
            <CardTitle className="flex items-center text-green-800">
              <>
                <PieChart className="w-6 h-6 mr-2" />
                <span>🌐 Canales de Venta</span>
              </>
            </CardTitle>
          </CardHeader>
          <CardContent>
            <CustomPieChart data={analyticsData.channelData} />
          </CardContent>
        </Card>
      </div>

      {/* Segmentación de Clientes Mejorada */}
      <Card className="bg-gradient-to-br from-purple-50 via-pink-50 to-rose-50 border border-purple-200">
        <CardHeader>
          <CardTitle className="flex items-center text-purple-800">
            <>
              <Users className="w-6 h-6 mr-2" />
              <span>👥 Segmentación Inteligente de Clientes</span>
            </>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {analyticsData.customerSegments.map(
              (segment: any, index: number) => (
                <div
                  key={index}
                  className="p-6 bg-white rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 border-l-4 border-l-purple-400"
                >
                  <div className="flex items-center justify-between mb-4">
                    <span className="text-3xl">{segment.emoji}</span>
                    <span className="text-sm font-bold text-purple-600 bg-purple-100 px-2 py-1 rounded-full">
                      {segment.count} clientes
                    </span>
                  </div>
                  <h4 className="font-bold text-gray-900 mb-2">
                    {segment.segment}
                  </h4>
                  <div className="space-y-2">
                    <div className="w-full bg-gray-200 rounded-full h-3">
                      <div
                        className="bg-gradient-to-r from-purple-400 to-pink-500 h-3 rounded-full transition-all duration-500"
                        style={{ width: `${segment.percentage}%` }}
                      ></div>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-gray-600">
                        {segment.percentage}% del total
                      </span>
                      <span className="font-bold text-green-600">
                        S/ {segment.revenue.toLocaleString()}
                      </span>
                    </div>
                  </div>
                </div>
              )
            )}
          </div>
        </CardContent>
      </Card>

      {/* Análisis por Horas */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card className="bg-gradient-to-br from-yellow-50 via-orange-50 to-red-50 border border-yellow-200">
          <CardHeader>
            <CardTitle className="flex items-center text-yellow-800">
              <>
                <Activity className="w-6 h-6 mr-2" />
                <span>⏰ Análisis por Horas del Día</span>
              </>
            </CardTitle>
          </CardHeader>
          <CardContent>
            <CustomBarChart data={hourlyData} />
          </CardContent>
        </Card>

        {/* Métricas de Tiempo Real */}
        <Card className="bg-gradient-to-br from-indigo-50 via-blue-50 to-cyan-50 border border-indigo-200">
          <CardHeader>
            <CardTitle className="flex items-center text-indigo-800">
              <>
                <Zap className="w-6 h-6 mr-2" />
                <span>⚡ Métricas en Tiempo Real</span>
              </>
            </CardTitle>
          </CardHeader>
          <CardContent>
            <CustomPieChart data={analyticsData.categoryData} />
            <div className="space-y-3 mt-4">
              <div className="flex items-center justify-between p-4 bg-white rounded-lg shadow-sm">
                <div className="flex items-center space-x-3">
                  <div className="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
                  <span className="font-medium">Visitantes Online</span>
                </div>
                <span className="text-2xl font-bold text-green-600">
                  {Math.round(mockCustomers.length * 0.1)}
                </span>
              </div>

              <div className="flex items-center justify-between p-4 bg-white rounded-lg shadow-sm">
                <div className="flex items-center space-x-3">
                  <div className="w-3 h-3 bg-blue-500 rounded-full animate-pulse"></div>
                  <span className="font-medium">Productos en Carrito</span>
                </div>
                <span className="text-2xl font-bold text-blue-600">
                  {Math.round(mockProducts.length * 0.05)}
                </span>
              </div>

              <div className="flex items-center justify-between p-4 bg-white rounded-lg shadow-sm">
                <div className="flex items-center space-x-3">
                  <div className="w-3 h-3 bg-purple-500 rounded-full animate-pulse"></div>
                  <span className="font-medium">Ventas Hoy</span>
                </div>
                <span className="text-2xl font-bold text-purple-600">
                  S/ {Math.round(mockTotalRevenue / 30).toLocaleString()}
                </span>
              </div>

              <div className="flex items-center justify-between p-4 bg-white rounded-lg shadow-sm">
                <div className="flex items-center space-x-3">
                  <div className="w-3 h-3 bg-yellow-500 rounded-full animate-pulse"></div>
                  <span className="font-medium">Conversión Actual</span>
                </div>
                <span className="text-2xl font-bold text-yellow-600">
                  {(Math.random() * 5 + 2).toFixed(1)}%
                </span>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Rendimiento por Categoría Mejorado */}
      <Card className="bg-gradient-to-br from-gray-50 to-slate-100 border border-gray-200">
        <CardHeader>
          <CardTitle className="flex items-center text-gray-800">
            <>
              <BarChart3 className="w-6 h-6 mr-2" />
              <span>🏆 Rendimiento por Categoría</span>
            </>
          </CardTitle>
        </CardHeader>
        <CardContent className="p-0">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gradient-to-r from-gray-100 to-gray-200">
                <tr>
                  <th className="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">
                    Categoría
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">
                    Productos
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">
                    Valor Inventario
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">
                    Margen
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">
                    Tendencia
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {analyticsData.productPerformance.map(
                  (product: any, index: number) => (
                    <tr
                      key={index}
                      className="hover:bg-gradient-to-r hover:from-yellow-50 hover:to-orange-50 transition-all duration-200"
                    >
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center space-x-3">
                          <span className="text-2xl">{product.emoji}</span>
                          <span className="font-medium text-gray-900">
                            {product.category}
                          </span>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-gray-900 font-medium">
                        {product.sales} unidades
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap font-bold text-green-600">
                        S/ {product.revenue.toLocaleString()}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span
                          className={`px-3 py-1 rounded-full text-sm font-bold ${
                            product.margin > 50
                              ? "bg-green-100 text-green-800"
                              : product.margin > 40
                              ? "bg-yellow-100 text-yellow-800"
                              : "bg-red-100 text-red-800"
                          }`}
                        >
                          {product.margin}%
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center space-x-2">
                          {product.trend === "up" && (
                            <>
                              <TrendingUp className="w-5 h-5 text-green-500" />
                              <span className="text-green-600 font-medium">
                                📈 Subiendo
                              </span>
                            </>
                          )}
                          {product.trend === "down" && (
                            <>
                              <TrendingUp className="w-5 h-5 text-red-500 rotate-180" />
                              <span className="text-red-600 font-medium">
                                📉 Bajando
                              </span>
                            </>
                          )}
                          {product.trend === "stable" && (
                            <>
                              <Activity className="w-5 h-5 text-gray-500" />
                              <span className="text-gray-600 font-medium">
                                ➡️ Estable
                              </span>
                            </>
                          )}
                        </div>
                      </td>
                    </tr>
                  )
                )}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
