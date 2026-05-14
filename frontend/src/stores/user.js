import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfo } from '../api/user'

export const useUserStore = defineStore('user', () => {
    const userInfo = ref(null)
    const isLoggedIn = ref(false)

    async function fetchUserInfo() {
        try {
            const res = await getUserInfo()
            userInfo.value = res.data
            isLoggedIn.value = true
            return res.data
        } catch {
            userInfo.value = null
            isLoggedIn.value = false
            return null
        }
    }

    function clearUser() {
        userInfo.value = null
        isLoggedIn.value = false
    }

    function setUser(data) {
        userInfo.value = data
        isLoggedIn.value = true
    }

    const isAdmin = () => userInfo.value?.role === 'ADMIN'

    return { userInfo, isLoggedIn, fetchUserInfo, clearUser, setUser, isAdmin }
})
