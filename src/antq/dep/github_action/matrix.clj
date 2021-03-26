(ns antq.dep.github-action.matrix)

(defn- matrix-variable-name
  [version]
  (when-let [[[_ vname]] (re-seq #"\$\{\{\s*matrix\.(.+?)\s*}}" version)]
    vname))

(defn expand-matrix-value
  [parsed-yaml job-name deps]
  (mapcat
   (fn [dep]
     (if-let [vname (some-> (:version dep)
                            (matrix-variable-name))]
       (let [values (-> (:jobs parsed-yaml)
                        (get-in  [job-name :strategy :matrix (keyword vname)]))]
         (map #(assoc dep
                      :version %
                      :only-newest-version? true)
              values))
       [dep]))
   deps))
