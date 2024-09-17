window.BENCHMARK_DATA = {
  "lastUpdate": 1726594334777,
  "repoUrl": "https://github.com/sya-ri/ktConfig",
  "entries": {
    "JMH Benchmark": [
      {
        "commit": {
          "author": {
            "email": "git@s7a.dev",
            "name": "sya-ri",
            "username": "sya-ri"
          },
          "committer": {
            "email": "git@s7a.dev",
            "name": "sya-ri",
            "username": "sya-ri"
          },
          "distinct": true,
          "id": "cf71df7375ec7962a260453ce8ad0408d6c6fd16",
          "message": "test: More benchmark tests",
          "timestamp": "2024-09-17T00:37:57+09:00",
          "tree_id": "517dea3f9c15339ea779838d22bbabb717d6797a",
          "url": "https://github.com/sya-ri/ktConfig/commit/cf71df7375ec7962a260453ce8ad0408d6c6fd16"
        },
        "date": 1726501875867,
        "tool": "jmh",
        "benches": [
          {
            "name": "dev.s7a.ktconfig.Benchmark.single",
            "value": 0.007407566238606649,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.list",
            "value": 0.013476180560264324,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.map",
            "value": 0.017057184969912306,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.recursive",
            "value": 0.08873301557621137,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveSingle",
            "value": 0.006095695304688169,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveList",
            "value": 0.010571310138731936,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveMap",
            "value": 0.014219913161913142,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveRecursive",
            "value": 0.07286711799090033,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
        ]
      },
      {
        "commit": {
          "author": {
            "email": "git@s7a.dev",
            "name": "sya-ri",
            "username": "sya-ri"
          },
          "committer": {
            "email": "git@s7a.dev",
            "name": "sya-ri",
            "username": "sya-ri"
          },
          "distinct": true,
          "id": "a69e6cadbdae220e2f5e84adf37fd740787f826e",
          "message": "chore: Bump spigot",
          "timestamp": "2024-09-17T20:30:40+09:00",
          "tree_id": "05555301f21e7328f95422657a4601b3d58003bc",
          "url": "https://github.com/sya-ri/ktConfig/commit/a69e6cadbdae220e2f5e84adf37fd740787f826e"
        },
        "date": 1726577371878,
        "tool": "jmh",
        "benches": [
          {
            "name": "dev.s7a.ktconfig.Benchmark.list",
            "value": 0.013938567701521317,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.map",
            "value": 0.016914886562283297,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.recursive",
            "value": 0.09089880134705758,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveList",
            "value": 0.010606264600763068,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveMap",
            "value": 0.014411759756884093,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveRecursive",
            "value": 0.07097902524563325,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveSingle",
            "value": 0.00603077782642675,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.single",
            "value": 0.007392714916739922,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "git@s7a.dev",
            "name": "sya-ri",
            "username": "sya-ri"
          },
          "committer": {
            "email": "git@s7a.dev",
            "name": "sya-ri",
            "username": "sya-ri"
          },
          "distinct": true,
          "id": "8567be6c037aaa4e2f1a3297d9588d1558195812",
          "message": "ci: Bump java to 21 lts",
          "timestamp": "2024-09-17T21:47:08+09:00",
          "tree_id": "ffdd2912645c836ec9a2b9ad0a38ee13b4bfade2",
          "url": "https://github.com/sya-ri/ktConfig/commit/8567be6c037aaa4e2f1a3297d9588d1558195812"
        },
        "date": 1726577831920,
        "tool": "jmh",
        "benches": [
          {
            "name": "dev.s7a.ktconfig.Benchmark.list",
            "value": 0.013393938174429002,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.map",
            "value": 0.016551309732107767,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.recursive",
            "value": 0.08627813960040932,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveList",
            "value": 0.010881132708316902,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveMap",
            "value": 0.014064645549614653,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveRecursive",
            "value": 0.07179679185943223,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveSingle",
            "value": 0.006165087868181304,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.single",
            "value": 0.00737963605623206,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "git@s7a.dev",
            "name": "sya-ri",
            "username": "sya-ri"
          },
          "committer": {
            "email": "git@s7a.dev",
            "name": "sya-ri",
            "username": "sya-ri"
          },
          "distinct": true,
          "id": "53e792b36bf294a352496da2eedb5905008444aa",
          "message": "ci: Bump java to 21 lts (more)",
          "timestamp": "2024-09-17T21:50:53+09:00",
          "tree_id": "7d2bf4fe643668c74521e14f80c5940be63d707e",
          "url": "https://github.com/sya-ri/ktConfig/commit/53e792b36bf294a352496da2eedb5905008444aa"
        },
        "date": 1726578068645,
        "tool": "jmh",
        "benches": [
          {
            "name": "dev.s7a.ktconfig.Benchmark.list",
            "value": 0.014084281804519797,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.map",
            "value": 0.017397960359941258,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.recursive",
            "value": 0.08424888132364665,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveList",
            "value": 0.01060871672847929,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveMap",
            "value": 0.01425604809970457,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveRecursive",
            "value": 0.0660047051440461,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveSingle",
            "value": 0.006199240561381083,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.single",
            "value": 0.007368244976930234,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          }
        ]
      },
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
          "id": "c5df97652653e720ac834dacb478f0a996a770df",
          "message": "fix(deps): update dependency com.github.seeseemelk:mockbukkit-v1.20 to v3.93.2",
          "timestamp": "2024-09-17T12:51:02Z",
          "url": "https://github.com/sya-ri/ktConfig/pull/195/commits/c5df97652653e720ac834dacb478f0a996a770df"
        },
        "date": 1726578105485,
        "tool": "jmh",
        "benches": [
          {
            "name": "dev.s7a.ktconfig.Benchmark.list",
            "value": 0.01392508320895409,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.map",
            "value": 0.017588440156381516,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.recursive",
            "value": 0.08362483245596664,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveList",
            "value": 0.01058031607197645,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveMap",
            "value": 0.014393960545397785,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveRecursive",
            "value": 0.06441306323665845,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveSingle",
            "value": 0.006461451697029927,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.single",
            "value": 0.00759898087555666,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "29139614+renovate[bot]@users.noreply.github.com",
            "name": "renovate[bot]",
            "username": "renovate[bot]"
          },
          "committer": {
            "email": "noreply@github.com",
            "name": "GitHub",
            "username": "web-flow"
          },
          "distinct": true,
          "id": "6d0c7af6b43204a09b8197fc095d069394f3303f",
          "message": "fix(deps): update dependency com.github.seeseemelk:mockbukkit-v1.20 to v3.93.2 (#195)\n\nCo-authored-by: renovate[bot] <29139614+renovate[bot]@users.noreply.github.com>",
          "timestamp": "2024-09-17T22:07:41+09:00",
          "tree_id": "7004653fe26d1b0e6fd16e2a9919a70fc537e904",
          "url": "https://github.com/sya-ri/ktConfig/commit/6d0c7af6b43204a09b8197fc095d069394f3303f"
        },
        "date": 1726579065710,
        "tool": "jmh",
        "benches": [
          {
            "name": "dev.s7a.ktconfig.Benchmark.list",
            "value": 0.013892535423781662,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.map",
            "value": 0.01728511538490719,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.recursive",
            "value": 0.08440629813287744,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveList",
            "value": 0.010983117706311838,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveMap",
            "value": 0.014412627966335584,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveRecursive",
            "value": 0.06596920838935283,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveSingle",
            "value": 0.006198178715526174,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.single",
            "value": 0.0075794480659665125,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "git@s7a.dev",
            "name": "sya-ri",
            "username": "sya-ri"
          },
          "committer": {
            "email": "git@s7a.dev",
            "name": "sya-ri",
            "username": "sya-ri"
          },
          "distinct": true,
          "id": "405e39ac78bf8d77ad383417694c612b902188e5",
          "message": "test: Add recursive tests",
          "timestamp": "2024-09-18T02:18:30+09:00",
          "tree_id": "a5c9166999f20c1ad39bc16596491194d0fb3894",
          "url": "https://github.com/sya-ri/ktConfig/commit/405e39ac78bf8d77ad383417694c612b902188e5"
        },
        "date": 1726594334460,
        "tool": "jmh",
        "benches": [
          {
            "name": "dev.s7a.ktconfig.Benchmark.list",
            "value": 0.014331703438826908,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.map",
            "value": 0.017509388921666008,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.recursive",
            "value": 0.08531521687252985,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveList",
            "value": 0.010984950639879547,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveMap",
            "value": 0.016209581582762023,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveRecursive",
            "value": 0.06483357219845116,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.saveSingle",
            "value": 0.0062065663479636485,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "dev.s7a.ktconfig.Benchmark.single",
            "value": 0.007553026417457163,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          }
        ]
      }
    ]
  }
}
