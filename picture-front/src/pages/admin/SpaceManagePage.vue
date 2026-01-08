<template>
  <div id="spaceManagePage">
    <a-flex justify="space-between">
      <h2>空间管理</h2>
      <a-space>
        <a-button type="primary" href="/add_space" target="_blank">+创建空间</a-button>
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px"></div>
    <!--    搜索框-->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="空间名称">
        <a-input
          v-model:value="searchParams.spaceName"
          placeholder="请输入空间名称"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="spaceLevel" label="空间级别">
        <a-select
          v-model:value="searchParams.spaceLevel"
          style="min-width: 180px"
          :options="SPACE_LEVEL_OPTIONS"
          placeholder="请输入空间级别"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="用户 id">
        <a-input v-model:value="searchParams.userId" placeholder="请输入用户 id" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <div style="margin-bottom: 16px" />
    <!--    表格-->
    <a-table
      :columns="columns"
      :data-source="datalist"
      :pagination="pagination"
      @change="doTableChange"
      :scroll="{ x: 'max-content' }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'spaceLevel'">
          <div>{{ SPACE_LEVEL_MAP[record.spaceLevel as keyof typeof SPACE_LEVEL_MAP] }}</div>
        </template>
        <template v-if="column.dataIndex === 'spaceUseInfo'">
          <div>大小:{{ formatSize(record.totalSize) }} / {{ formatSize(record.maxSize) }}</div>
          <div>数量:{{ record.totalCount }} / {{ record.maxCount }}</div>
        </template>
        <!--        创建时间-->
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <!--        编辑时间-->
        <template v-else-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <!--        操作-->
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button type="link" :href="`/add_space?id=${record.id}`" target="_blank"
              >编辑</a-button
            >
            <a-button danger @click="doDelete(record.id)">删除</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { SPACE_LEVEL_MAP, SPACE_LEVEL_OPTIONS } from '@/constants/space'
import { formatSize } from '@/utils'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: '空间名称',
    dataIndex: 'spaceName',
  },
  {
    title: '空间级别',
    dataIndex: 'spaceLevel',
  },
  {
    title: '使用情况',
    dataIndex: 'spaceUseInfo',
  },
  {
    title: '用户 id',
    dataIndex: 'userId',
    width: 80,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '编辑时间',
    dataIndex: 'editTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

const datalist = ref<API.Space[]>([])
const total = ref(0)

// 搜索条件
const searchParams = ref<API.SpaceeQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 分页功能
const pagination = computed(() => {
  return {
    current: searchParams.value.current,
    pageSize: searchParams.value.pageSize,
    total: total,
    showSizeChanger: true,
    showTotal: (total) => `共${total}条`,
  }
})

// 表格发生变化时，重新获取数据
const doTableChange = (page: any) => {
  searchParams.value.current = page.current
  searchParams.value.pageSize = page.pageSize
  fetchData()
}

// 获取数据
const fetchData = async () => {
  // TODO: 等待后端 API 实现
  // const res = await listSpaceByPageUsingPost({
  //   ...searchParams.value,
  // })
  // if (res.data.code === 0 && res.data.data) {
  //   datalist.value = res.data.data.records ?? []
  //   total.value = res.data.data.total ?? 0
  // } else {
  //   message.error('获取数据失败' + res.data.message)
  // }
  message.warning('空间管理 API 暂未实现')
}

// 页面加载时获取数据，只请求一次
onMounted(() => {
  fetchData()
})

// 搜索框功能
const doSearch = (value: any) => {
  // 重置页码
  searchParams.value.current = 1
  fetchData()
}

// 删除功能
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  // TODO: 等待后端 API 实现
  // const res = await deleteSpaceUsingPost({ id })
  // if (res.data.code === 0) {
  //   message.success('删除成功')
  //   fetchData()
  // } else {
  //   message.error('删除失败' + res.data.message)
  // }
  message.warning('删除功能 API 暂未实现')
}
</script>
