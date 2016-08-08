
define ['_', 'lscache'], (un, lscache)->
  return {
    set: (key, value)->
      if _.isObject value
        value = JSON.stringify(value)

      lscache.set key, value, 30
    get: (key)->
      data = lscache.get key
      try
        data = JSON.parse data
      catch e
      return data

    remove: (key)->
      lscache.remove key
    delete: (key)->
      lscache.remove key
    rm: (key)->
      lscache.remove key
  }
