export default [
  {
    url: '/api/user/login',
    method: 'post',
    response: () => {
      return {
        code: 0,
        data: {
          id: 1,
          userName: 'testUser'
        },
        message: 'ok'
      }
    }
  }
]
