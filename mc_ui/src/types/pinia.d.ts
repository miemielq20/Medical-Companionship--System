// pinia-plugin-persistedstate 类型扩展
import 'pinia'

declare module 'pinia' {
  export interface DefineStoreOptionsBase<S, Store> {
    persist?: boolean | {
      enabled?: boolean
      storage?: Storage
      key?: string
      pick?: Array<keyof S>
      serializer?: {
        serialize: (value: S) => string
        deserialize: (value: string) => S
      }
      beforeRestore?: (context: any) => void
      afterRestore?: (context: any) => void
    }
  }
}
