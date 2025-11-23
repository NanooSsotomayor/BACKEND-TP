# Sistema Viajes (Monorepo)

Este repositorio contiene varios microservicios Java/Maven y datos para OSRM.

Notas:
- La carpeta `osrm-data/` contiene archivos grandes — no está recomendada su subida al repositorio.
- Si necesitas mantener archivos grandes en el control de versiones, considera usar Git LFS o almacenamiento externo (S3, FTP).

Estructura:
- api-gateway/
- microservicio-inventario/
- microservicio-solicitudes/
- microservicio-tarifas/
- microservicio-usuarios/
- microservicio-viajes/
- osrm-data/  (recomendado: no incluir en Git)

Pasos rápidos para subir a GitHub (PowerShell):

```powershell
cd C:\sistema-viajes-ms
git config --global user.name "Tu Nombre"
git config --global user.email "tu@correo.com"
# Inicializar
git init -b main
# Añadir y commitear
git add .
git commit -m "Initial commit"
# Crear repo remoto con gh CLI (opcional)
# gh repo create <usuario>/<repo> --private --source=. --remote=origin --push --confirm
# O añadir remote manualmente (después de crear en GitHub web):
# git remote add origin https://github.com/<usuario>/<repo>.git
# git push -u origin main
```

