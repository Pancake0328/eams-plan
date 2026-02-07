import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import { setupPermissionDirective } from './directives/permission'

const app = createApp(App)
const pinia = createPinia()

// 注册 Pinia
app.use(pinia)

// 注册路由
app.use(router)

// 注册 Element Plus
app.use(ElementPlus)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 注册权限指令
setupPermissionDirective(app)

app.mount('#app')
