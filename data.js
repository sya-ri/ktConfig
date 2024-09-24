window.BENCHMARK_DATA = {
  "lastUpdate": 1727144924867,
  "repoUrl": "https://github.com/sya-ri/ktConfig",
  "entries": {
    "JMH Benchmark": [
      {
        "commit": {
          "author": {
            "name": "sya-ri",
            "username": "sya-ri"
          },
          "committer": {
            "name": "sya-ri",
            "username": "sya-ri"
          },
          "id": "005140362be5a784666a23c77a8892938143b57b",
          "message": "chore(deps): update dependency gradle to v8.10.2",
          "timestamp": "2024-09-17T17:22:12Z",
          "url": "https://github.com/sya-ri/ktConfig/pull/212/commits/005140362be5a784666a23c77a8892938143b57b"
        },
        "date": 1727144924582,
        "tool": "jmh",
        "benches": [
          {
            "name": "dev.s7a.ktconfig.Benchmark.list",
            "value": 0.014601626922659068,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.map",
            "value": 0.01726095728369005,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.recursive",
            "value": 0.08274831248497688,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveList",
            "value": 0.011430444604606398,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveMap",
            "value": 0.016236599318683178,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveRecursive",
            "value": 0.06282239409737743,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveSingle",
            "value": 0.006302253520888096,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.single",
            "value": 0.007723200620546457,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          }
        ]
      }
    ]
  }
}