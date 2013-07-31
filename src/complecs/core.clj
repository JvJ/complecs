(ns complecs.core
  (:use
   potemkin)
  (:require
   complecs.state-machine
   complecs.entity-component-system))

(import-vars
 [complecs.state-machine
  
  init-state-machine
  ]

 [complecs.entity-component-system

  defcomponent])
 
