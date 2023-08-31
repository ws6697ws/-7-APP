package quantum.algorithm

import quantum.randGen
import quantum.topo.*
import utils.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.*

open class TestAlgorithm(topo: Topo, val allowRecoveryPaths: Boolean = true) : Algorithm(topo) {
    override val name: String = "TestAlgorithm" + if (allowRecoveryPaths) "" else "-R"

    override fun prepare() {
    }

    val pathHashSet = HashSet<MutableList<Node>>()
    val majorPathResult = HashMap<PickedPath, Int>()
    val candidatePathResult = HashMap<candidatePickedPath, Int>()
    val majorPaths = mutableListOf<PickedPath>()
    val recoveryPaths = HashMap<PickedPath, LinkedList<PickedPath>>()

    // Added Code
    val candidatePathList = mutableListOf<candidatePickedPath>()
    val allPathMap = HashMap<PickedPath, LinkedList<PickedPath>>()
    val allPathList = HashMap<PickedPath, LinkedList<PickedPath>>()

    // Added Code
    //val edgeMap1 = HashMap<Edge, Triple<HashMap<PickedPath, Int>, Int, Pair<DoubleArray, Int>>>()
    val edgeMap1 = HashMap<Edge, HashMap<PickedPath, Int>>()
    val edgeMap2 = HashMap<Edge, Int>()
    val edgeMap3 = HashMap<Edge, DoubleArray>()
    val edgeMap4 = HashMap<Edge, Int>()
    val edgeMap5 = HashMap<Edge, DoubleArray>()
    val linkMap = HashMap<Edge, Int>()

    val nodeCount = HashMap<Node, Int>()
    val nodeRecord = HashSet<Node>()

    val candidatePaths = mutableListOf<PickedPath>()

    override fun P2() {
        require({ topo.isClean() })
        majorPaths.clear()
        recoveryPaths.clear()
        candidatePathList.clear()
        pathHashSet.clear()

        // Added Code
        edgeMap1.clear()
        edgeMap2.clear()
        edgeMap3.clear()
        edgeMap4.clear()
        edgeMap5.clear()

        /*
        linkMap.clear()
        for (linkI in topo.links) {
            val tEdge = Edge(Pair(linkI.n1, linkI.n2))
            if (!linkMap.containsKey(tEdge)) {
                linkMap.put(tEdge, 0)
            }
            val tNum = linkMap[tEdge]!!
            linkMap[tEdge] = tNum + 1
        }
         */

        while (true) {
            val candidates = calCandidates(srcDstPairs)

            val pick = candidates.maxBy { it.first }
            if (pick != null && pick.first > 0.0) {
                pickAndAssignPath(pick)
                assignEdge(pick)
                pathHashSet.add(pick.third.toMutableList())
            } else {
                break
            }
        }

        if (allowRecoveryPaths)
            P2Extra()

        // Added Code
        for ((k, v) in edgeMap2) {
            edgeMap3.put(k, DoubleArray(edgeMap2[k]!!+1, {0.0}))
            edgeMap4.put(k, 0)
            edgeMap5.put(k, DoubleArray(edgeMap2[k]!!+1, {0.0}))
        }
        P2AddedFunction()

        for ((k, v) in edgeMap3) {
            val tmpSi = v.size
            var tmpValu = 1.0
            for (h in (0..tmpSi-1)) {
                edgeMap5[k]!![h] = tmpValu - edgeMap3[k]!![h]
                tmpValu = edgeMap5[k]!![h]
            }
        }
        /*
        for ((k, v) in edgeMap2) {
            nodeRecord.add(k.n1)
            nodeRecord.add(k.n2)

            if (!nodeCount.containsKey(k.n1)) {
                nodeCount.put(k.n1, 0)
            }
            if (!nodeCount.containsKey(k.n2)) {
                nodeCount.put(k.n2, 0)
            }
            val tmpN1 = Math.max(nodeCount[k.n1]!!, v)
            val tmpN2 = Math.max(nodeCount[k.n2]!!, v)

            nodeCount.put(k.n1, tmpN1)
            nodeCount.put(k.n2, tmpN2)
        }

         */
        val thi=1
        while (true) {
            val addedCandidates = calCandidates6(srcDstPairs)

            var pick1 = addedCandidates.maxBy { it.first }

            if (pick1==null) {
                println("empty")
                break
            }


            val ty = pick1.third.first.edges()
            var tnum=0
            for (te in ty) {
                tnum+=1
                val ss1 = pick1.third.second[tnum-1].first
                val ss = pick1.third.second[tnum].first
                val rr = te.n1.links.filter { !it.assigned && it.contains(te.n2)}.size
                val kk =  Math.min(rr, Math.min(pick1.third.first[tnum-1].remainingQubits - ss1 - ss, pick1.third.first[tnum].remainingQubits-ss))
                if (kk < 0) {
                    val kj=0
                }
            }

            var isAssign = false

            //var pick = completePath(pick1!!)
            var pick = pick1
            val tmpPath = pick!!.third.first
            val tmpLen = tmpPath.size
            for (iii in 1..tmpLen-1) {
                if (pick!!.third.second[iii].first > 0) {
                    isAssign = true
                    break
                }
            }

            if (isAssign || !pathHashSet.contains(tmpPath)) {
                addedPickAndAssignPath(pick)
                addedAssignEdge(pick)
                pathHashSet.add(tmpPath)
            } else {
                break
            }
        }

    }

    fun completePath(ppick:candidatePickedPath) : candidatePickedPath {
        var tmpPick = ppick
        val ttmpPath = ppick.third.first
        val tmpArr = IntArray(ttmpPath.size, {0})
        (0..ttmpPath.size-2).forEach { i ->

            var tmpDataFirst = ppick.third.second[i+1].first
            var tmpDataSecond = ppick.third.second[i+1].second
            var tmpDataThird = ppick.third.second[i+1].third.clone()
            val wid=ttmpPath[i].links.filter { !it.assigned && it.contains(ttmpPath[i + 1]) }.size-tmpDataFirst
            var minw=Math.min(wid, ttmpPath[i].remainingQubits-tmpArr[i]-tmpDataFirst-ppick.third.second[i].first)
            minw=Math.min(minw, ttmpPath[i+1].remainingQubits-tmpArr[i+1]-tmpDataFirst)
            if (minw <0) {
                var vd = 1
            }
            if (minw>0) {
                var vd = 1
                if(minw>0){

                    val tmpNewArray = DoubleArray(minw+1, {0.0})
                    val tmpProba = topo.edgeProbability(Edge(Pair(ttmpPath[i], ttmpPath[i+1])))
                    for (jj in 0..minw) {
                        val pro = C_(minw, jj) * tmpProba.pow(jj) * (1 - tmpProba).pow(minw- jj)
                        tmpNewArray[jj] = pro
                    }
                    val tmpResultArray = DoubleArray(minw+tmpDataThird.size, {0.0})
                    for (jjj in 0..tmpNewArray.size-1) {
                        for (kkk in 0..tmpDataThird.size-1) {
                            tmpResultArray[jjj+kkk] += tmpDataThird[kkk] * tmpNewArray[jjj]
                        }
                    }
                    tmpPick.third.second[i+1] = Triple(tmpDataFirst+minw, tmpDataSecond+minw, tmpResultArray.clone())
                    tmpArr[i] += minw
                    tmpArr[i+1] += minw
                }
            }
        }
        return tmpPick
    }

    fun P2AddedFunction() {
        for (major in majorPaths) {
            val majorP = major.third
            val majorWid = major.second
            val tempM = Array<Array<Double>>(majorP.edges().size) {ii->Array<Double>(majorWid+1){jj->0.0}}
            for (i in 0..majorP.edges().size-1) {
                val tempEdge = majorP.edges()[i]
                val tempNum = edgeMap1[tempEdge]!![major]!!
                val tempProbability = topo.edgeProbability(tempEdge)
                var startPro = 1.0
                for (j in (0..majorWid)) {
                    var bb1=C_(tempNum, j)
                    var bb2=tempProbability.pow(j)
                    var bb3=(1-tempProbability).pow(tempNum-j)
                    val pro = C_(tempNum, j) * tempProbability.pow(j) * (1-tempProbability).pow(tempNum-j)
                    startPro = startPro - pro
                    tempM[i][j] = startPro
                }
            }
            /*
            for (i in (0..majorP.size-2)) {
                val tempEdge = Edge(Pair(majorP[i], majorP[i+1]))
                val tempNum = edgeMap1[tempEdge]!![major]
                for (j in (0..majorWid)) {
                    val pro = C_(tempNum!!, j)
                }
            }
            */

            for (i in 0..majorP.edges().size-1) {
                var sEdge : Int
                var eEdge : Int
                sEdge = Math.max(0, i-topo.k)
                eEdge = Math.min(majorP.edges().size-1, i+topo.k)
                val externalPro = Array<Double>(majorWid+1){jj->0.0}
                var prevVal = 1.0
                for (k in 0..majorWid) {
                    var tempVal = 1.0
                    for (j in sEdge.rangeTo(eEdge))  {
                        if (j == i) {
                            continue
                        }
                        tempVal = tempVal * tempM[j][k]
                    }
                    externalPro[k] = prevVal - tempVal
                    prevVal = tempVal
                }

                val tempEdge = majorP.edges()[i]
                val internalSize = edgeMap1[tempEdge]!![major]!!
                val internalPro = Array<Double>(internalSize+1){jj->0.0}
                val tempProbability = topo.edgeProbability(tempEdge)
                for (j in 0..internalSize) {
                    internalPro[j] = C_(internalSize, j) * tempProbability.pow(j) * (1-tempProbability).pow(internalSize-j)
                }

                var edgeRemainPro = Array<Double>(internalSize+1){jj->0.0}

                for (j in 0..externalPro.size-1) {
                    for (k in 0..internalPro.size-1) {
                        val tempCur:Int
                        if (k <= j) {
                            tempCur = 0
                        } else {
                            tempCur = k - j
                        }
                        val tempPro = externalPro[j] * internalPro[k]
                        edgeRemainPro[tempCur] = edgeRemainPro[tempCur] + tempPro
                    }
                }

                if (majorP.edges().size == 1) {
                    edgeRemainPro = Array<Double>(internalSize+1){jj->0.0}
                    edgeRemainPro[0] = 1.0
                }

                val ttttt=0
                if (edgeMap4[tempEdge] == 0) {
                    edgeMap4[tempEdge] = internalSize
                    for (j in 0..edgeRemainPro.size-1) {
                        edgeMap3[tempEdge]!![j] = edgeRemainPro[j]
                    }
                } else {
                    val remainNum = edgeMap4[tempEdge]!!
                    val newRemainNum = remainNum + edgeRemainPro.size-1
                    val allRemainPro = Array<Double>(newRemainNum+1){jj->0.0}
                    for (j in 0..remainNum) {
                        for (k in 0..edgeRemainPro.size-1) {
                            val tempCur = j+k
                            val tempPro = edgeMap3[tempEdge]!![j] * edgeRemainPro[k]
                            allRemainPro[tempCur] += tempPro
                        }
                    }
                    for (j in 0..allRemainPro.size-1) {
                        edgeMap3[tempEdge]!![j] = allRemainPro[j]
                    }
                    edgeMap4[tempEdge] = newRemainNum
                }

            }
        }
    }

    fun P2Extra() {
        majorPaths.forEach { majorPath ->
            val (_, _, p) = majorPath
            (0..p.size - 2).forEach { i ->
                val rp=LinkedList<Node>()
                rp.add(p[i])
                rp.add(p[i+1])
                val wid=p[i].links.filter { !it.assigned && it.contains(p[i + 1]) }
                if (wid.isNotEmpty()) {
                    var minw=Math.min(wid.size, p[i].remainingQubits)
                    minw=Math.min(minw, p[i+1].remainingQubits)
                    if(minw>0){
                        val pick: PickedPath = 1.0 to minw also rp
                        pickAndAssignPath(pick, majorPath)
                        assignEdge(pick, majorPath)
                    }
                }
            }
        }
    }

    // Added Code
    fun assignEdge(pick:PickedPath, majorPath:PickedPath? = null) {
        val pp = pick.third

        for (edgeI in pp.edges()) {
            if (!edgeMap1.containsKey(edgeI)) {
                edgeMap1[edgeI] = HashMap<PickedPath, Int>()
                edgeMap2[edgeI] = 0
            }
            if (majorPath == null) {
                edgeMap1[edgeI]!!.put(pick, pick.second)
                edgeMap2[edgeI] = edgeMap2[edgeI]!! + pick.second
            } else {
                val temp = edgeMap1[edgeI]!!
                val wid = edgeMap1[edgeI]!![majorPath]
                edgeMap1[edgeI]!!.put(majorPath, wid!!+pick.second)
                val temp1 = edgeMap1[edgeI]!!
                edgeMap2[edgeI] = edgeMap2[edgeI]!! + pick.second
            }
        }
        /*
        for (index in (0..pp.size-2)) {
            val temp = Edge(Pair(pp[index], pp[index+1]))
            if (!edgeMap1.containsKey(temp)) {
                edgeMap1[temp] = HashMap<PickedPath, Int>()
            }
            if (majorPath == null) {
                edgeMap1[temp]?.put(pick, pick.second)
                edgeMap2[temp] = edgeMap2[temp]!! + pick.second
            } else {
                val wid = edgeMap1[temp]?.get(majorPath)
                edgeMap1[temp]?.put(majorPath, wid!!+pick.second)
                edgeMap2[temp] = edgeMap2[temp]!! + pick.second
            }

        }
        */
    }

    fun addedAssignEdge(pick:candidatePickedPath) {
        val pp = pick.third.first
        var ttnum = 0
        for (edgeI in pp.edges()) {
            ttnum += 1
            if (!edgeMap2.containsKey(edgeI)) {
                edgeMap2[edgeI] = 0
            }
            edgeMap2[edgeI] = edgeMap2[edgeI]!! + pick.third.second[ttnum].first
        }

        val tempM = Array<DoubleArray>(pp.edges().size) {ii->DoubleArray(pick.second+1){jj->0.0}}
        for (i in 0..pp.edges().size-1) {
            var startPro = 1.0
            for (j in 0..pick.second) {
                val pro = pick.third.second[i+1].third[j]
                startPro = startPro - pro
                tempM[i][j] = startPro
            }
        }
        for (i in 0..pp.edges().size-1) {
            var sEdge: Int
            var eEdge: Int
            sEdge = Math.max(0, i - topo.k)
            eEdge = Math.min(pp.edges().size - 1, i + topo.k)
            val externalPro = DoubleArray(pick.second + 1) { jj -> 0.0 }
            var prevVal = 1.0
            for (k in 0..pick.second) {
                var tempVal = 1.0
                for (j in sEdge.rangeTo(eEdge)) {
                    if (j == i) continue
                    tempVal = tempVal * tempM[j][k]
                }
                externalPro[k] = prevVal - tempVal
                prevVal = tempVal
            }

            val internalSize = pick.third.second[i+1].second
            var internalPro = pick.third.second[i+1].third.clone()
            val tempEdge = pp.edges()[i]
            val edgeRemainPro = DoubleArray(internalSize+1){jj->0.0}

            for (j in 0..externalPro.size-1) {
                for (k in 0..internalPro.size-1) {
                    val tempCur:Int
                    if (k <= j) {
                        tempCur = 0
                    } else {
                        tempCur = k - j
                    }
                    val tempPro = externalPro[j] * internalPro[k]
                    edgeRemainPro[tempCur] = edgeRemainPro[tempCur] + tempPro
                }
            }

            if (!edgeMap3.containsKey(tempEdge)) {
                edgeMap3[tempEdge] = doubleArrayOf()
            }
            edgeMap3[tempEdge] = edgeRemainPro.clone()

            if (!edgeMap5.containsKey(tempEdge)) {
                edgeMap5[tempEdge] = doubleArrayOf()
            }
            edgeMap5[tempEdge] = DoubleArray(edgeMap3[tempEdge]!!.size)
            var tttempV = 1.0
            for (iii in 0..edgeMap3[tempEdge]!!.size-1) {
                edgeMap5[tempEdge]!![iii] = tttempV - edgeMap3[tempEdge]!![iii]
                tttempV = edgeMap5[tempEdge]!![iii]
            }

        }
    }


    // if majorPath is null: *pick* is a major path
    // else: *pick* is a recovery path
    fun pickAndAssignPath(pick: PickedPath, majorPath: PickedPath? = null) {
        if (majorPath != null)
            recoveryPaths[majorPath]!!.add(pick)
        else {
            majorPaths.add(pick)
            recoveryPaths[pick] = LinkedList<PickedPath>()
        }

        val width = pick.second
        val toAdd = Triple(mutableListOf<LinkBundle>(), width, mutableMapOf<Edge, List<Pair<Connection, Int>>>())

        pick.third.edges().forEach { (n1, n2) ->
            val links = n1.links.filter { !it.assigned && it.contains(n2) }.sortedBy { it.id }.subList(0, width)
            require({ links.size == width })
            toAdd.first.add(links)
            links.forEach {
                it.assignQubits()
                it.tryEntanglement() // just for display
            }
        }
    }

    fun addedPickAndAssignPath(candidatePath: candidatePickedPath) {
        candidatePathList.add(candidatePath)


        //val toAdd = Triple(mutableListOf<LinkBundle>(), width, mutableMapOf<Edge, List<Pair<Connection, Int>>>())

        var ttnum = 0
        candidatePath.third.first.edges().forEach { (n1, n2) ->
            ttnum += 1
            val width = candidatePath.third.second[ttnum].first
            val aa1 = n1.links.filter {  it.contains(n2) }.sortedBy { it.id }
            val aa = n1.links.filter { !it.assigned && it.contains(n2) }.sortedBy { it.id }
            if (aa.size < width) {
                val a = 1
            } else if (aa.size > width) {
                val a = 1
            }
            val links = n1.links.filter { !it.assigned && it.contains(n2) }.sortedBy { it.id }.subList(0, width)
            require({ links.size == width })
            //toAdd.first.add(links)
            links.forEach {
                it.assignQubits()
                it.tryEntanglement() // just for display
            }
        }
    }


    //Added code
    fun calCandidates6(ops: List<Pair<Node, Node>>): List<candidatePickedPath> {
        return ops.pmap fxx@{ (src1, dst1) ->
            var maxM = 0
            //val maxM = Math.min(src.remainingQubits, dst.remainingQubits)
            val availLinks = topo.links.filter {
                !it.assigned
            }.groupBy { it.n1 to it.n2 }.map {it.key}.toHashSet()

            availLinks.forEach { tempEdge ->
                var availNum = tempEdge.n1.links.filter{!it.assigned && it.contains(tempEdge.n2)}.size
                availNum = Math.min(availNum, Math.min(tempEdge.n1.remainingQubits, tempEdge.n2.remainingQubits))
                if (availNum > maxM) {
                    maxM = availNum
                }
            }


            //if (maxM == 0) return@fxx null
            var maxEExt = 0.0

            var candidate: candidatePickedPath? = null
            var src : Node
            var dst : Node
            for (w in (maxM downTo 0)) {
                for (qq in (1..2)) {
                    if (qq == 1) {
                        src = src1
                        dst = dst1
                    } else {
                        src = dst1
                        dst = src1
                    }
                    val haoTotalE = HashMap<Edge, Int>()
                    val haoArrayE = HashMap<Edge, DoubleArray>()
                    val haoE4 = HashMap<Edge, Int>()
                    val E1 = HashMap<Int, Double>()
                    val E2 = HashMap<Int, DoubleArray>()
                    val E3 = HashMap<Int, Int>()
                    val E4 = HashMap<Int, Int>()
                    val totalE = HashMap<Int, Int>()
                    val arrayE = HashMap<Int, DoubleArray>()

                    val q = PriorityQueue<Edge>(Comparator { (o1, _), (o2, _) ->
                        -E1[o1.id]!!.compareTo(E1[o2.id]!!)
                    })

                    val prevFromSrc: HashMap<Node, Node> = hashMapOf()
                    fun validate(n: Node, isR: Boolean) :Pair<Boolean, LinkedList<Node>>{
                        var cur = n
                        val path = LinkedList<Node>()
                        var isAssigned = false
                        while (cur != topo.sentinal) {
                            if (isR) {
                                path.addLast(cur)
                            } else {
                                path.addFirst(cur)
                            }

                            if (prevFromSrc[cur]!! != topo.sentinal) {
                                if (haoE4[cur to prevFromSrc[cur]!!]!!>0) {
                                    isAssigned = true
                                }
                            }
                            cur = prevFromSrc[cur]!!
                        }
                        return Pair(isAssigned, path)

                    }
                    fun getPathFromSrc1(n: Node, isR: Boolean): Pair<MutableList<Node>, MutableList<Triple<Int, Int, DoubleArray>>> {
                        val path = LinkedList<Node>()
                        val path1 : List<Node>

                        val outputP2 = LinkedList<Triple<Int, Int, DoubleArray>>()
                        var cur = n
                        while (cur != topo.sentinal) {
                            path.addFirst(cur)
                            cur = prevFromSrc[cur]!!
                        }
                        if (isR) {
                            path1=path.reversed()
                        } else{
                            path1=path.toList()
                        }
                        val tmpEdgs = path1.edges()
                        for (hi in tmpEdgs) {
                            outputP2.addLast(Triple(haoE4[hi]!!,haoTotalE[hi]!!,haoArrayE[hi]!!.clone() ))

                        }
                        outputP2.addFirst(Triple(0,Int.MAX_VALUE,doubleArrayOf()))


                        return Pair(path1.toMutableList(), outputP2.toMutableList())
                    }

                    fun getPathFromSrc(n: Node, isR: Boolean): Pair<MutableList<Node>, MutableList<Triple<Int, Int, DoubleArray>>> {
                        val path = LinkedList<Node>()
                        val assignPath = LinkedList<Int>()
                        val totalPath = LinkedList<Int>()
                        val arrayPath = LinkedList<DoubleArray>()

                        var cur = n
                        while (cur != topo.sentinal) {
                            if (isR) {
                                path.addLast(cur)
                                assignPath.addLast(E4[cur.id]!!)
                                totalPath.addLast(totalE[cur.id]!!)
                                arrayPath.addLast(arrayE[cur.id]!!.clone())
                            } else {
                                path.addFirst(cur)
                                assignPath.addFirst(E4[cur.id]!!)
                                totalPath.addFirst(totalE[cur.id]!!)
                                arrayPath.addFirst(arrayE[cur.id]!!.clone())
                            }
                            cur = prevFromSrc[cur]!!
                        }
                        if (isR) {
                            assignPath.addFirst(assignPath.last)
                            totalPath.addFirst(totalPath.last)
                            arrayPath.addFirst(arrayPath.last)
                            assignPath.removeLast()
                            totalPath.removeLast()
                            arrayPath.removeLast()
                        }
                        val outputP1 = LinkedList<Node>()
                        val outputP2 = LinkedList<Triple<Int, Int, DoubleArray>>()
                        val ttttSize = path.size
                        for (kk in (0..ttttSize-1)) {
                            outputP1.addLast(path.pollFirst())
                            outputP2.addLast(Triple(assignPath.pollFirst(), totalPath.pollFirst(), arrayPath.pollFirst()))
                        }
                        return Pair(outputP1.toMutableList(), outputP2.toMutableList())
                    }

                    fun getPath(n: Node): MutableList<Node> {
                        val path = LinkedList<Node>()


                        var cur = n
                        while (cur != topo.sentinal) {

                            path.addFirst(cur)

                            cur = prevFromSrc[cur]!!
                        }

                        return path.toMutableList()
                    }

                    /*
                    fun getPathFromSrc(n: Node): MutableList<Node> {
                        val path = LinkedList<Node>()

                        var cur = n
                        while (cur != topo.sentinal) {
                            path.addFirst(cur)
                            cur = prevFromSrc[cur]!!
                        }
                        return path.toMutableList()
                    }
                     */


                    E1[src.id] = Double.POSITIVE_INFINITY
                    E4[src.id] = 0
                    haoE4[src to topo.sentinal] = 0
                    totalE[src.id] = Int.MAX_VALUE
                    arrayE[src.id] = doubleArrayOf()
                    //E2[src.id] = DoubleArray(w+1, {1.0})
                    //E3[src.id] = 0
                    //E4[src.id] = Int.MAX_VALUE

                    //E[src.id] = Double.POSITIVE_INFINITY to DoubleArray(w + 1, { 0.0 })
                    q.offer(src to topo.sentinal)

                    while (q.isNotEmpty()) {
                        val (u, prev) = q.poll()  // invariant: top of q reveals the node with highest e under m
                        if (u in prevFromSrc) continue  // skip same node suboptimal paths
                        prevFromSrc[u] = prev // record

                        if (u == dst) {
                            //candidate = E1[u.id].first to w also getPathFromSrc(dst)
                            if (E1[u.id]!! > maxEExt) {
                                println("Current MaxM:" + maxM)
                                println("Current W:" + w)
                                println("Current Reverse:" + (qq==2))
                                println("Current Ext:" + E1[u.id]!!)
                                println("\n")

                                var ttmpP : Pair<MutableList<Node>, MutableList<Triple<Int, Int, DoubleArray>>>
                                if (qq == 1) {
                                    ttmpP = getPathFromSrc1(dst, false)
                                } else {
                                    ttmpP = getPathFromSrc1(dst, true)
                                }
                                candidate = E1[u.id]!! to E3[u.id]!! also ttmpP
                                maxEExt = E1[u.id]!!

                                val ty = ttmpP.first.edges()
                                var tnum=0
                                for (te in ty) {
                                    tnum+=1
                                    var ss = ttmpP.second[tnum].first
                                    var jj = haoE4[te]
                                    var rr = te.n1.links.filter { !it.assigned && it.contains(te.n2)}.size
                                    if (rr < ss) {
                                        val bh=rr
                                        val k=0
                                        if (rr < jj!!) {
                                            val kj=ss

                                        }

                                    }
                                }
                            }
                            break
                        }
                        if (E1[u.id]!! < maxEExt) {
                            break
                        }

                        val neighborList =
                            u.links.filter { (!(it.theOtherEndOf(u) in prevFromSrc)) }.groupBy { it.n1 to it.n2 }.map{it.key}
                                .toHashSet()
                        for (it in neighborList) {
                            val oth = it.otherThan(u)
                            if (oth in prevFromSrc) continue



                            val lenNum = u.links.filter { !it.assigned && it.contains(oth) }.size
                            val assignNum = Math.min(
                                w,
                                Math.min(lenNum, Math.min(u.remainingQubits - haoE4[u to prevFromSrc[u]!!]!!, oth.remainingQubits))
                            )

                            if (oth == dst) {
                                var (isA, pa) = validate(u, qq==2)
                                if (qq == 2) {
                                    pa.addFirst(oth)
                                } else {
                                    pa.addLast(oth)
                                }
                                if (isA == false && pathHashSet.contains(pa)) continue
                            }
                            var isGo = false
                            var tmpNewArray = doubleArrayOf()

                            var totalArray = doubleArrayOf()
                            var totalNum = 0
                            if (assignNum<0){
                                val ki=0
                            }

                            if (assignNum > 0) {
                                isGo = true
                                val curArray = DoubleArray(assignNum + 1)
                                val tmpProba = topo.edgeProbability(it)
                                for (j in (0..assignNum)) {
                                    val pro = C_(assignNum, j) * tmpProba.pow(j) * (1 - tmpProba).pow(assignNum - j)
                                    curArray[j] = pro
                                }

                                if (edgeMap3.contains(it)) {
                                    val tmpPrevP = edgeMap3[it]!!.clone()
                                    val tmpPrevWid = tmpPrevP.size - 1
                                    var ttArray = DoubleArray(assignNum + tmpPrevWid + 1, { 0.0 })
                                    for (j in (0..tmpPrevWid)) {
                                        for (k in (0..assignNum)) {
                                            ttArray[j + k] += tmpPrevP[j] * curArray[k]
                                        }
                                    }
                                    totalArray = ttArray.clone()
                                    totalNum = ttArray.size-1
                                    var startPro = 1.0
                                    tmpNewArray = DoubleArray(assignNum + tmpPrevWid + 1, { 0.0 })
                                    for (j in (0..assignNum + tmpPrevWid)) {
                                        startPro = startPro - ttArray[j]
                                        tmpNewArray[j] = startPro
                                    }

                                } else {
                                    totalArray = curArray.clone()
                                    totalNum = curArray.size-1
                                    tmpNewArray = DoubleArray(assignNum + 1, { 0.0 })
                                    var startPro = 1.0
                                    for (j in (0..assignNum)) {
                                        startPro = startPro - curArray[j]
                                        tmpNewArray[j] = startPro
                                    }
                                }

                            } else {
                                if (edgeMap3.contains(it)) {
                                    isGo = true
                                    tmpNewArray = edgeMap5[it]!!.clone()
                                    totalArray = edgeMap3[it]!!.clone()
                                    totalNum = edgeMap3[it]!!.size-1
                                }
                            }
                            if (isGo) {
                                var newResult: Double
                                var newWid: Int
                                var newResultArray: DoubleArray
                                if (u == src) {
                                    newWid = tmpNewArray.size - 1
                                    newResultArray = tmpNewArray.clone()
                                } else {
                                    newWid = Math.min(E3[u.id]!!, tmpNewArray.size - 1)
                                    newResultArray = DoubleArray(newWid + 1)
                                    for (k in (0..newWid)) {
                                        newResultArray[k] = E2[u.id]!![k] * tmpNewArray[k]
                                    }
                                }
                                var newExactArray = DoubleArray(newWid + 1)
                                var tmpVV = 1.0
                                for (k in (0..newWid)) {
                                    newExactArray[k] = tmpVV - newResultArray[k]
                                    tmpVV = newResultArray[k]
                                }
                                if (u == src) {
                                    var tmpExt = 0.0
                                    for (k in (0..newWid)) {
                                        tmpExt += newExactArray[k] * k
                                    }
                                    newResult = tmpExt
                                } else {
                                    val pathLong = getPath(u).size-1
                                    var tmpExt = 0.0
                                    for (k in (1..newWid)) {
                                        var tmpExt1 = 0.0
                                        for (kkk in (k..newWid)) {
                                            tmpExt1 += C_(kkk, k) * topo.q.pow(pathLong * k) * (1-topo.q.pow(pathLong)).pow(kkk-k) * newExactArray[kkk]
                                        }
                                        tmpExt += tmpExt1 * k
                                    }
                                    newResult = tmpExt
                                }

                                if (!E1.containsKey(oth.id) || (newResult > E1[oth.id]!!)) {
                                    E1.put(oth.id, newResult)
                                    E2.put(oth.id, newResultArray.clone())
                                    E3.put(oth.id, newWid)
                                    if(oth.links.filter { !it.assigned && it.contains(u)}.size < assignNum) {
                                        val ij=0
                                    }
                                    haoE4.put(u to oth, assignNum)
                                    haoTotalE.put(u to oth, totalNum)
                                    haoArrayE.put(u to oth, totalArray.clone())

                                    E4.put(oth.id, assignNum)
                                    totalE.put(oth.id, totalNum)
                                    arrayE.put(oth.id, totalArray.clone())
                                    q.offer(oth to u)
                                }
                            }
                        }
                    }
                }
            }



            candidate
        }.filter { it != null } as List<candidatePickedPath>
    }
    /*
    fun calCandidates3(ops: List<Pair<Node, Node>>): List<PickedPath> {
        return ops.pmap fxx@{ (src, dst) ->
            var maxM = 0
            //val maxM = Math.min(src.remainingQubits, dst.remainingQubits)
            val availLinks = topo.links.filter {
                !it.assigned
            }.groupBy { it.n1 to it.n2 }.map(Edge(it.key)).toHashSet()

            availLinks.forEach { tempEdge ->
                var availNum = topo.links.filter{!it.assigned && (tempEdge.equals(Edge(it.n1, it.n2)))}.size
                availNum = Math.min(availNum, Math.min(tempEdge.n1.remainingQubits, tempEdge.n2.remainingQubits))
                if (availNum > maxM) {
                    maxM = availNum
                }
            }


            if (maxM == 0) return@fxx null

            var candidate: PickedPath? = null

            for (w in (maxM downTo 1)) {
                val E1 = HashMap<Int, Double>()
                val E2 = HashMap<Int, DoubleArray>()
                val E3 = HashMap<Int, Int>()
                val E4 = HashMap<Int, Int>()
                val probaArrayE = HashMap<Int, DoubleArray>()

                val q = PriorityQueue<Edge>(Comparator { (o1, _), (o2, _) ->
                    -E1[o1.id]!!.compareTo(E1[o2.id]!!)
                })

                val prevFromSrc: HashMap<Node, Node> = hashMapOf()
                fun getPathFromSrc(n: Node): MutableList<Pair<Node, Int>> {
                    val path = LinkedList<Pair<Node, Int>>()

                    var cur = n
                    while (cur != topo.sentinal) {
                        path.addFirst(Pair(cur, E4[cur.id]!!))
                        cur = prevFromSrc[cur]!!
                    }
                    return path.toMutableList()
                }
                /*
                fun getPathFromSrc(n: Node): MutableList<Node> {
                    val path = LinkedList<Node>()

                    var cur = n
                    while (cur != topo.sentinal) {
                        path.addFirst(cur)
                        cur = prevFromSrc[cur]!!
                    }
                    return path.toMutableList()
                }
                 */


                E1[src.id] = Double.POSITIVE_INFINITY
                //E2[src.id] = DoubleArray(w+1, {1.0})
                //E3[src.id] = 0
                //E4[src.id] = Int.MAX_VALUE

                //E[src.id] = Double.POSITIVE_INFINITY to DoubleArray(w + 1, { 0.0 })
                q.offer(src to topo.sentinal)

                while (q.isNotEmpty()) {
                    val (u, prev) = q.poll()  // invariant: top of q reveals the node with highest e under m
                    if (u in prevFromSrc) continue  // skip same node suboptimal paths
                    prevFromSrc[u] = prev // record

                    if (u == dst) {
                        //candidate = E1[u.id].first to w also getPathFromSrc(dst)
                        candidate = E1[u.id] to w also getPathFromSrc(dst)
                        break
                    }

                    val neighborList = topo.links.filter{it.contains(u)}.groupBy { it.n1 to it.n2 }.map(Edge(it.key)).toHashSet()
                    neighborList.forEach { it ->
                        val oth = it.otherThan(u)
                        val lenNum = topo.links.filter{!it.assigned && it.contains(u) && it.contains(oth)}.size
                        val assignNum = Math.min(w, Math.min(lenNum, Math.min(u.remainingQubits-E4[u.id]!!, oth.remainingQubits)))
                        var isGo = false
                        var tmpNewArray = doubleArrayOf()
                        if (assignNum > 0) {
                            isGo = true
                            val curArray = DoubleArray(assignNum + 1)
                            val tmpProba = topo.edgeProbability(it)
                            for (j in (0..assignNum)) {
                                val pro = C_(assignNum, j) * tmpProba.pow(j) * (1 - tmpProba).pow(assignNum - j)
                                curArray[j] = pro
                            }

                            if (edgeMap1.contains(it)) {
                                val tmpPrevP = edgeMap3[it]!!.clone()
                                val tmpPrevWid = tmpPrevP.size - 1
                                var ttArray = DoubleArray(assignNum + tmpPrevWid + 1, {0.0})
                                for (j in (0..tmpPrevWid)) {
                                    for (k in (0..assignNum)) {
                                        ttArray[j+k] += tmpPrevP[j] * curArray[k]
                                    }
                                }
                                var startPro = 1.0
                                tmpNewArray = DoubleArray(assignNum + tmpPrevWid + 1, {0.0})
                                for (j in (0..assignNum + tmpPrevWid)) {
                                    startPro = startPro - ttArray[j]
                                    tmpNewArray[j] = startPro
                                }

                            } else {
                                tmpNewArray = DoubleArray(assignNum + 1, {0.0})
                                var startPro = 1.0
                                for (j in (0..assignNum)) {
                                    startPro = startPro - curArray[j]
                                    tmpNewArray[j] = startPro
                                }
                            }

                        } else {
                            if (edgeMap1.contains(it)) {
                                isGo = true
                                tmpNewArray = edgeMap5[it]!!.clone()
                            }
                        }
                        if (isGo) {
                            var newResult : Double
                            var newWid : Int
                            var newResultArray : DoubleArray
                            if (u == src) {
                                newWid = tmpNewArray.size-1
                                newResultArray = tmpNewArray.clone()
                            } else {
                                newWid = Math.min(E3[u.id]!!, tmpNewArray.size-1)
                                newResultArray = DoubleArray(newWid+1)
                                for (k in (0..newWid)) {
                                    newResultArray[k] = E2[u.id]!![k] * tmpNewArray[k]
                                }
                            }
                            var newExactArray = DoubleArray(newWid+1)
                            var tmpVV = 1.0
                            for (k in (0..newWid)) {
                                newExactArray[k] = tmpVV-newResultArray[k]
                                tmpVV = newResultArray[k]
                            }
                            var tmpExt = 0.0
                            for (k in (0..newWid)) {
                                tmpExt += newExactArray[k] * k
                            }
                            newResult = tmpExt * (getPathFromSrc(u).size-1)

                            if (!E1.containsKey(oth.id) || (newResult > E1[oth.id]!!)) {
                                E1.put(oth.id, newResult)
                                E2.put(oth.id, newResultArray.clone())
                                E3.put(oth.id, newWid)
                                E4.put(oth.id, assignNum)
                                probaArrayE.put(oth.id, tmp)
                                q.offer(oth to u)
                            }
                        }
                    }
                }
            }

            if (candidate != null) break



            candidate
        }.filter { it != null } as List<PickedPath>
    }

    fun calCandidates2(ops: List<Pair<Node, Node>>): List<PickedPath> {
        return ops.pmap fxx@{ (src, dst) ->
            var maxM : Int
            //val maxM = Math.min(src.remainingQubits, dst.remainingQubits)
            val srcLinks = topo.links.filter {
                it.n1 == src || it.n2 == src
            }.groupBy { it.n1 to it.n2 }.map(Edge(it.key)).toHashSet()
            val dstLinks = topo.links.filter {
                it.n1 == dst || it.n2 == dst
            }.groupBy { it.n1 to it.n2 }.map(Edge(it.key)).toHashSet()

            var maxSrc = 0
            var maxDst = 0
            srcLinks.forEach { tempEdge ->
                val oldW : Int
                if (edgeMap2.containsKey(tempEdge)) {
                    oldW = edgeMap2[tempEdge]!!
                } else {
                    oldW = 0
                }
                val linkNum = topo.links.filter {
                    !it.assigned && (tempEdge.equals(Edge(it.n1, it.n2)))
                }.size
                maxSrc = Math.max(maxSrc, Math.min(linkNum, src.remainingQubits) + oldW)
            }
            dstLinks.forEach { tempEdge ->
                val oldW : Int
                if (edgeMap2.containsKey(tempEdge)) {
                    oldW = edgeMap2[tempEdge]!!
                } else {
                    oldW = 0
                }
                val linkNum = topo.links.filter {
                    !it.assigned && (tempEdge.equals(Edge(it.n1, it.n2)))
                }.size
                maxDst = Math.max(maxDst, Math.min(linkNum, dst.remainingQubits) + oldW)
            }
            maxM = Math.min(maxSrc, maxDst)

            if (maxM == 0) return@fxx null

            var candidate: PickedPath? = null

            for (w in (maxM downTo 1)) {
                val prevFromSrc: HashMap<Node, Node> = hashMapOf()
                fun getPathFromSrc(n: Node): MutableList<Node> {
                    val path = LinkedList<Node>()

                    var cur = n
                    while (cur != topo.sentinal) {
                        path.addFirst(cur)
                        cur = prevFromSrc[cur]!!
                    }
                    return path.toMutableList()
                }
                val E1 = HashMap<Int, Double>()
                val E2 = HashMap<Int, DoubleArray>()
                val E3 = HashMap<Int, Int>()

                val q = PriorityQueue<Edge>(Comparator { (o1, _), (o2, _) ->
                    -E1[o1.id]!!.compareTo(E1[o2.id]!!)
                })

                E1[src.id] = Double.POSITIVE_INFINITY
                E2[src.id] = DoubleArray(w+1, {1.0})
                E3[src.id] = 0

                //E[src.id] = Double.POSITIVE_INFINITY to DoubleArray(w + 1, { 0.0 })
                q.offer(src to topo.sentinal)

                while (q.isNotEmpty()) {
                    val (u, prev) = q.poll()  // invariant: top of q reveals the node with highest e under m
                    if (u in prevFromSrc) continue  // skip same node suboptimal paths
                    prevFromSrc[u] = prev // record

                    if (u == dst) {
                        //candidate = E1[u.id].first to w also getPathFromSrc(dst)
                        candidate = E1[u.id] to w also getPathFromSrc(dst)
                        break
                    }

                    val neighborList = topo.links.filter{it.contains(u)}.groupBy { it.n1 to it.n2 }.map(Edge(it.key)).toHashSet()
                    neighborList.forEach { it ->
                        val oth = it.otherThan(u)

                    }
                }
            }

            if (candidate != null) break



            candidate
        }.filter { it != null } as List<PickedPath>
    }

    fun calCandidates1(ops: List<Pair<Node, Node>>): List<PickedPath> {
        return ops.pmap fxx@{ (src, dst) ->
            var maxM : Int
            //val maxM = Math.min(src.remainingQubits, dst.remainingQubits)
            var srcNum = topo.links.filter {
                !it.assigned && (it.n1 == src || it.n2 == src )
            }.groupBy { it.n1 to it.n2 }.toList().sortedBy{(_, value)->value.size}.first().value.size

            var dstNum = topo.links.filter {
                !it.assigned && (it.n1 == dst || it.n2 == dst )
            }.groupBy { it.n1 to it.n2 }.toList().sortedBy{(_, value)->value.size}.first().value.size

            var srcCount = 0
            if (nodeCount.containsKey(src)) {
                srcCount = nodeCount[src]!!
            }
            var dstCount = 0
            if (nodeCount.containsKey(dst)) {
                dstCount = nodeCount[dst]!!
            }

            srcNum = Math.max(Math.min(srcNum, src.remainingQubits), srcCount)
            dstNum = Math.max(Math.min(dstNum, dst.remainingQubits), dstCount)

            maxM = Math.min(srcNum, dstNum)


            //maxM = Math.min(Math.min(srcNum, src.remainingQubits), Math.min(dstNum, dst))


            if (maxM == 0) return@fxx null

            var candidate: PickedPath? = null

            for (w in (maxM downTo 1)) {

                var edgesPre = topo.links.filter {
                    !it.assigned
                }.groupBy { it.n1 to it.n2 }.filter { it.value.size >= w }.map { Edge(it.key) }.toHashSet()

                edgesPre.filter {((it.n1 in nodeRecord || it.n1 == src || it.n1 == dst) && it.n1.remainingQubits >= w) || (!(it.n1 in nodeRecord || it.n1 == src || it.n1 == dst) && it.n1.remainingQubits >= 2*w)
                        && (((it.n2 in nodeRecord || it.n2 == src || it.n2 == dst) && it.n2.remainingQubits >= w) || (!(it.n2 in nodeRecord || it.n2 == src || it.n2 == dst) && it.n2.remainingQubits >= 2*w))
                }
                val nodeNote = HashMap<Node, Int>()

                val neighborsOf = HashMap<Node, HashSet<Node>>()

                for (k in edgesPre) {
                    neighborsOf[k.n1]!!.add(k.n2)
                    neighborsOf[k.n2]!!.add(k.n1)
                    if (!nodeNote.containsKey(k.n1)) {
                        nodeNote.put(k.n1, 0)
                    }
                    if (!nodeNote.containsKey(k.n2)) {
                        nodeNote.put(k.n2, 0)
                    }
                    nodeNote.put(k.n1, nodeNote[k.n1]!!+1)
                    nodeNote.put(k.n2, nodeNote[k.n2]!!+1)
                }

                for (k in edgeMap2) {
                    if (k.value >= w) {
                        neighborsOf[k.key.n1]!!.add(k.key.n2)
                        neighborsOf[k.key.n2]!!.add(k.key.n1)
                    }
                }

                if (neighborsOf[src]!!.isEmpty() || neighborsOf[dst]!!.isEmpty())
                    continue

                val prevFromSrc: HashMap<Node, Node> = hashMapOf()
                fun getPathFromSrc(n: Node): MutableList<Node> {
                    val path = LinkedList<Node>()

                    var cur = n
                    while (cur != topo.sentinal) {
                        path.addFirst(cur)
                        cur = prevFromSrc[cur]!!
                    }
                    return path.toMutableList()
                }



                val E1 = HashMap<Int, Double>()
                val E2 = HashMap<Int, DoubleArray>()
                val E3 = HashMap<Int, Int>()

                for ((tempNode, _) in neighborsOf) {
                    E1.put(tempNode.id, Double.NEGATIVE_INFINITY)
                    E2.put(tempNode.id, DoubleArray(w+1))
                    E3.put(tempNode.id, 0)
                }

                //val E = topo.nodes.map { Double.NEGATIVE_INFINITY to DoubleArray(w + 1, { 0.0 }) }.toTypedArray()
                val q = PriorityQueue<Edge>(Comparator { (o1, _), (o2, _) ->
                    -E1[o1.id]!!.compareTo(E1[o2.id]!!)
                })

                E1[src.id] = Double.POSITIVE_INFINITY

                //E[src.id] = Double.POSITIVE_INFINITY to DoubleArray(w + 1, { 0.0 })
                q.offer(src to topo.sentinal)

                while (q.isNotEmpty()) {
                    val (u, prev) = q.poll()  // invariant: top of q reveals the node with highest e under m
                    if (u in prevFromSrc) continue  // skip same node suboptimal paths
                    prevFromSrc[u] = prev // record

                    if (u == dst) {
                        //candidate = E1[u.id].first to w also getPathFromSrc(dst)
                        candidate = E1[u.id] to w also getPathFromSrc(dst)
                        break
                    }

                    neighborsOf[u].forEach { neighbor ->
                        val tmpEdge = Edge(Pair(u, neighbor))
                        val tmpOldP = E2[u.id]!!.clone()
                        val tmpOldWid = E3[u.id]!!
                        val tmpCurP : DoubleArray
                        val tmpCurWid : Int
                        if (edgeMap1.contains(tmpEdge)) {
                            tmpCurP = edgeMap5[tmpEdge]!!.clone()
                            tmpCurWid = tmpCurP.size-1
                        } else {
                            val qubitNum1 : Int
                            if (u == src || u == dst) {
                                qubitNum1 = u.remainingQubits
                            } else {
                                if (edgeMap1.contains(Edge(Pair(prevFromSrc[u]!!, u)))) {
                                    qubitNum1 = u.remainingQubits
                                } else {
                                    qubitNum1 = u.remainingQubits - w
                                }

                            }
                            val qubitNum2 : Int
                            if (neighbor == src || neighbor == dst) {
                                qubitNum2 = neighbor.remainingQubits
                            } else {
                                if (edgeMap1.contains(Edge(Pair(prevFromSrc[u]!!, u)))) {
                                    qubitNum1 = u.remainingQubits
                                } else {
                                    qubitNum1 = u.remainingQubits - w
                                }

                            }

                            val linkNum = topo.links.filter{!it.assigned && (it.contains(u) && it.contains(neighbor))}.size
                            tmpCurWid = Math.min(Math.min(qubitNum1, qubitNum2), linkNum)
                            tmpCurP = DoubleArray(tmpCurWid+1)
                            val tmpProba = topo.edgeProbability(tmpEdge)
                            var startPro = 1.0
                            for (j in (0..tmpCurP.size-1)) {
                                val pro = C_(tmpCurWid!!, j) * tmpProba.pow(j) * (1-tmpProba).pow(tmpCurWid-j)
                                startPro = startPro - pro
                                tmpCurP[j] = startPro
                            }
                        }
                        val tmpIsStart : Boolean
                        val tmpWid : Int
                        if (u.id == src.id) {
                            tmpIsStart = true
                            tmpWid = tmpCurWid
                        } else {
                            tmpIsStart = false
                            tmpWid = Math.min(tmpOldWid, tmpCurWid)
                        }
                        //val (newWid, newResult, newArray) = topo.newE(tmpEdge, tmpIsStart, w, tmpOldP, tmpCurP)

                        var tmpArray= DoubleArray(w+1)
                        if (tmpIsStart) {
                            for (j in 0..w) {
                                tmpArray[j] = tmpCurP[j]
                            }
                        } else {
                            for (j in 0..w) {
                                tmpArray[j] = tmpCurP[j]*tmpOldP[j]
                            }
                        }
                        var newArray= DoubleArray(w+1)
                        var tmpVV = 1.0
                        for (j in 0..w) {
                            newArray[j] = tmpVV - tmpArray[j]
                            tmpVV = newArray[j]
                        }

                        val nodeTimes = getPathFromSrc(u).size

                        val newResult = (0..w).map { m -> m * newArray[m] }.sum() * topo.q.pow(nodeTimes)

                        if (E1[neighbor.id]!! < newResult) {
                            E1[neighbor.id] = newResult
                            E2[neighbor.id] = tmpArray.clone()
                            E3[neighbor.id] = tmpCurWid
                        }
                    }
                    /*
                    neighborsOf[u].forEach { neighbor ->
                        val tmp = E[u.id].second.clone()
                        val e = topo.e(getPathFromSrc(u) + neighbor, w, tmp)
                        val newE = e to tmp
                        val oldE = E[neighbor.id]

                        if (oldE.first < newE.first) {
                            E[neighbor.id] = newE
                            q.offer(neighbor to u)
                        }
                    }

                     */

                }


                if (candidate != null) break
            }

            candidate
        }.filter { it != null } as List<PickedPath>
    }

    fun addedCalCandidates(ops: List<Pair<Node, Node>>): List<candidatePickedPath> {
        return ops.pmap fxx@{ (src, dst) ->
            val remainingNodes = HashSet<Node>()
            val edgesPre = topo.links.filter {
                !it.assigned && it.n1.remainingQubits > 0 && it.n1.remainingQubits > 0
            }
            val edges = edgesPre.groupBy { it.n1 to it.n2 }.map { Edge(it.key) }.toHashSet()
            /*
            val edges = topo.links.filter {
                !it.assigned && it.n1.remainingQubits > 0 && it.n1.remainingQubits > 0
            }.groupBy { it.n1 to it.n2 }.map { Edge(it.key) }.toHashSet()

             */


            for (k in edges) {
                remainingNodes.add(k.n1)
                remainingNodes.add(k.n2)
            }
            for ((k, v) in edgeMap2) {
                edges.add(k)
                remainingNodes.add(k.n1)
                remainingNodes.add(k.n2)
            }


            val neighborsOf = HashMap<Node, HashSet<Node>>()

            for (k in edges) {
                neighborsOf[k.n1]!!.add(k.n2)
                neighborsOf[k.n2]!!.add(k.n1)
            }

            var candidate: candidatePickedPath? = null



            /*
            val neighborsOf = ReducibleLazyEvaluation<Node, MutableList<Node>>({ mutableListOf() })
            edges.forEach {
                neighborsOf[it.n1].add(it.n2)
                neighborsOf[it.n2].add(it.n1)
            }
            for ((k, v) in edgeMap2) {
                val tempN1 = k.toList()[0]
                val tempN2 = k.toList()[1]
                neighborsOf[tempN1].add(tempN2)
                neighborsOf[tempN2].add(tempN1)
            }
             */

            val prevFromSrc: HashMap<Node, Node> = hashMapOf()
            fun getPathFromSrc(n: Node): MutableList<Node> {
                val path = LinkedList<Node>()

                var cur = n
                while (cur != topo.sentinal) {
                    path.addFirst(cur)
                    cur = prevFromSrc[cur]!!
                }
                return path.toMutableList()
            }

            val E1 = HashMap<Int, Double>()
            val E2 = HashMap<Int, DoubleArray>()
            val E3 = HashMap<Int, Int>()

            for (tempNode in remainingNodes) {
                E1.put(tempNode.id, Double.NEGATIVE_INFINITY)
                E2.put(tempNode.id, doubleArrayOf())
                E3.put(tempNode.id, 0)
            }

            //val E = topo.nodes.map { Double.NEGATIVE_INFINITY to DoubleArray(w + 1, { 0.0 }) }.toTypedArray()
            val q = PriorityQueue<Edge>(Comparator { (o1, _), (o2, _) ->
                -E1[o1.id]!!.compareTo(E1[o2.id]!!)
            })

            E1[src.id] = Double.POSITIVE_INFINITY

            //E[src.id] = Double.POSITIVE_INFINITY to DoubleArray(w + 1, { 0.0 })
            q.offer(src to topo.sentinal)

            while (q.isNotEmpty()) {
                val (u, prev) = q.poll()  // invariant: top of q reveals the node with highest e under m
                if (u in prevFromSrc) continue  // skip same node suboptimal paths
                prevFromSrc[u] = prev // record

                if (u == dst) {
                    //candidate = E1[u.id].first to w also getPathFromSrc(dst)
                    candidate = E1[u.id] to E3[u.id] also getPathFromSrc(dst)
                    break
                }

                neighborsOf[u].forEach { neighbor ->
                    val tmpEdge = Edge(Pair(u, neighbor))
                    val tmpOldP = E2[u.id]!!.clone()
                    val tmpOldWid = E3[u.id]!!
                    val tmpCurP : DoubleArray
                    val tmpCurWid : Int
                    if (edgeMap1.contains(tmpEdge)) {
                        tmpCurP = edgeMap5[tmpEdge]!!.clone()
                        tmpCurWid = tmpCurP.size-1
                    } else {
                        val qubitNum1 = u.remainingQubits
                        val qubitNum2 = neighbor.remainingQubits
                        val linkNum = edgesPre.filter{it.contains(u) && it.contains(neighbor)}.size
                        tmpCurWid = Math.min(Math.min(qubitNum1, qubitNum2), linkNum)
                        tmpCurP = DoubleArray(tmpCurWid+1)
                        val tmpProba = topo.edgeProbability(tmpEdge)
                        var startPro = 1.0
                        for (j in (0..tmpCurP.size-1)) {
                            val pro = C_(tmpCurWid!!, j) * tmpProba.pow(j) * (1-tmpProba).pow(tmpCurWid-j)
                            startPro = startPro - pro
                            tmpCurP[j] = startPro
                        }
                    }
                    val tmpIsStart : Boolean
                    val tmpWid : Int
                    if (u.id == src.id) {
                        tmpIsStart = true
                        tmpWid = tmpCurWid
                    } else {
                        tmpIsStart = false
                        tmpWid = Math.min(tmpOldWid, tmpCurWid)
                    }
                    val (newWid, newResult, newArray) = topo.newE(tmpEdge, tmpIsStart, tmpWid, tmpOldP, tmpCurP)

                    if (E1[neighbor.id]!! < newResult) {

                    }
                }
                /*
                neighborsOf[u].forEach { neighbor ->
                    val tmp = E[u.id].second.clone()
                    val e = topo.e(getPathFromSrc(u) + neighbor, w, tmp)
                    val newE = e to tmp
                    val oldE = E[neighbor.id]

                    if (oldE.first < newE.first) {
                        E[neighbor.id] = newE
                        q.offer(neighbor to u)
                    }
                }

                 */

            }

            candidate
        }.filter { it != null } as List<candidatePickedPath>
    }

     */

    fun calCandidates(ops: List<Pair<Node, Node>>): List<PickedPath> {
        return ops.pmap fxx@{ (src, dst) ->
            val maxM = Math.min(src.remainingQubits, dst.remainingQubits)
            if (maxM == 0) return@fxx null

            var candidate: PickedPath? = null

            for (w in (maxM downTo 1)) {
                val failNodes = (topo.nodes - src - dst).filter { it.remainingQubits < 2 * w }.toHashSet()

                val edges = topo.links.filter {
                    !it.assigned && it.n1 !in failNodes && it.n2 !in failNodes
                }.groupBy { it.n1 to it.n2 }.filter { it.value.size >= w }.map { it.key }.toHashSet()

                val neighborsOf = ReducibleLazyEvaluation<Node, MutableList<Node>>({ mutableListOf() })

                edges.forEach {
                    neighborsOf[it.n1].add(it.n2)
                    neighborsOf[it.n2].add(it.n1)
                }

                if (neighborsOf[src].isEmpty() || neighborsOf[dst].isEmpty())
                    continue

                val prevFromSrc: HashMap<Node, Node> = hashMapOf()

                fun getPathFromSrc(n: Node): MutableList<Node> {
                    val path = LinkedList<Node>()

                    var cur = n
                    while (cur != topo.sentinal) {
                        path.addFirst(cur)
                        cur = prevFromSrc[cur]!!
                    }
                    return path.toMutableList()
                }

                val E = topo.nodes.map { Double.NEGATIVE_INFINITY to DoubleArray(w + 1, { 0.0 }) }.toTypedArray()
                val q = PriorityQueue<Edge>(Comparator { (o1, _), (o2, _) ->
                    -E[o1.id].first.compareTo(E[o2.id].first)
                })

                E[src.id] = Double.POSITIVE_INFINITY to DoubleArray(w + 1, { 0.0 })
                q.offer(src to topo.sentinal)

                while (q.isNotEmpty()) {
                    val (u, prev) = q.poll()  // invariant: top of q reveals the node with highest e under m
                    if (u in prevFromSrc) continue  // skip same node suboptimal paths
                    prevFromSrc[u] = prev // record

                    if (u == dst) {
                        candidate = E[u.id].first to w also getPathFromSrc(dst)
                        break
                    }

                    neighborsOf[u].forEach { neighbor ->
                        val tmp = E[u.id].second.clone()
                        val e = topo.e(getPathFromSrc(u) + neighbor, w, tmp)
                        val newE = e to tmp
                        val oldE = E[neighbor.id]

                        if (oldE.first < newE.first) {
                            E[neighbor.id] = newE
                            q.offer(neighbor to u)
                        }
                    }
                }

                if (candidate != null) break
            }

            candidate
        }.filter { it != null } as List<PickedPath>
    }

    //Added code
    fun addedP4Swapping(width:Int, tmpPath:Path) {
        val ttmpEdges = tmpPath.edges()
        if (ttmpEdges.size == 1) {
            val ttmpEdge1 = ttmpEdges[0]
            val entangledSize = ttmpEdge1.n1.links.filter{
                it.entangled && it.contains(ttmpEdge1.n2) && !it.utilized
            }.size
            val availSize = Math.min(width, entangledSize)
            val collectedLinks1 = ttmpEdge1.n1.links.filter{
                it.entangled && it.contains(ttmpEdge1.n2) && !it.utilized
            }.sortedBy { it.id }.take(availSize)
            for (tmpLink in collectedLinks1) {
                tmpLink.utilize()
            }

        } else {
            val edgeInternal = IntArray(ttmpEdges.size)
            val edgeExternal = IntArray(ttmpEdges.size)
            val edgeUtilize = IntArray(ttmpEdges.size)
            val edgeCollect = Array<ArrayList<Link>>(ttmpEdges.size, { it -> arrayListOf() })
            for (iii in 0..ttmpEdges.size - 1) {
                val ttmpEdge = ttmpEdges[iii]
                edgeInternal[iii] = ttmpEdge.n1.links.filter {
                    it.entangled && it.contains(ttmpEdge.n2) && !it.utilized
                }.size
            }
            for (iii in 0..ttmpEdges.size - 1) {
                var ssEdge: Int
                var eeEdge: Int
                val ttmpEdge = ttmpEdges[iii]
                ssEdge = Math.max(0, iii - topo.k)
                eeEdge = Math.min(ttmpEdges.size - 1, iii + topo.k)
                var tmpV = width
                for (jjj in ssEdge.rangeTo(eeEdge)) {
                    if (iii == jjj) continue
                    tmpV = Math.min(tmpV, edgeInternal[jjj])
                }
                edgeExternal[iii] = tmpV
                edgeUtilize[iii] = Math.min(edgeExternal[iii], edgeInternal[iii])
                val collectedLinks = ttmpEdge.n1.links.filter {
                    it.entangled && it.contains(ttmpEdge.n2) && !it.utilized
                }.sortedBy { it.id }.take(edgeUtilize[iii])
                edgeCollect[iii] = ArrayList<Link>()
                for (jjj in 0..edgeUtilize[iii] - 1) {
                    edgeCollect[iii].add(collectedLinks.get(jjj))
                    edgeCollect[iii][jjj].utilize()
                }

            }
            for (iii in 0..ttmpEdges.size - 2) {
                val ttmpEdge = ttmpEdges[iii]
                val swapNum = Math.min(edgeUtilize[iii], edgeUtilize[iii + 1])
                for (jjj in 0..swapNum - 1) {
                    val prevLink = edgeCollect[iii][jjj]
                    val nextLink = edgeCollect[iii + 1][jjj]
                    ttmpEdge.n2.attemptSwapping(prevLink, nextLink)
                }
            }
        }
    }
    fun addedP4() {
        majorPaths.forEach { pathWithWidth ->
            val (_, width, majorPath) = pathWithWidth
            addedP4Swapping(width, majorPath)
        }

        candidatePathList.forEach {  pathWithWidth ->
            val (_, width, pickPath) = pathWithWidth
            val majorPath = pickPath.first
            addedP4Swapping(width, majorPath)
        }
        val jjj=0
    }
    override fun P4() {
        addedP4()
    }
    /*
    override fun P4() {
        majorPaths.forEach { pathWithWidth ->
            val (_, width, majorPath) = pathWithWidth
            val oldNumOfPairs = topo.getEstablishedEntanglements(majorPath.first(), majorPath.last()).size  // just for logging

            for (w in (1..width)) {   // for w-width major path, treat it as w different paths, and repair separately
                // find all broken edges on the major path
                if(majorPath.size>2){
                    var available=LinkedList<Int>()
                    (1..(majorPath.size-1)).forEach{i->
                        if(majorPath[i-1].links.filter { it.entangled && it.contains(majorPath[i])}.isNotEmpty()){
                            available.add(1)
                        }
                        else  available.add(0)
                    }
                    (1..(majorPath.size-2)).forEach{i->
                        if(i<=topo.k){
                            if(majorPath.size-i-1<=topo.k){
                                if(available.sum()==majorPath.size-1){
                                    val prevLinks = majorPath[i].links.filter { it.entangled && !it.swappedAt(majorPath[i]) && it.contains(majorPath[i-1]) && !it.utilized}.sortedBy { it.id }.take(1)
                                    val nextLinks = majorPath[i].links.filter { it.entangled && !it.swappedAt(majorPath[i]) && it.contains(majorPath[i+1]) && !it.utilized}.sortedBy { it.id }.take(1)

                                    prevLinks.zip(nextLinks).forEach { (l1, l2) ->
                                        majorPath[i].attemptSwapping(l1, l2)
                                        if(majorPath[i-1]==majorPath.first()){
                                            l1.utilize()
                                        }
                                        if(majorPath[i+1]==majorPath.last()){
                                            l2.utilize()
                                        }
                                    }
                                }
                            }
                            else{
                                var flag=true
                                (0..(i+topo.k-1)).forEach { j->
                                    if(available[j]==0) {
                                        flag=false
                                    }
                                }
                                if(flag){
                                    val prevLinks = majorPath[i].links.filter { it.entangled && !it.swappedAt(majorPath[i]) && it.contains(majorPath[i-1]) && !it.utilized}.sortedBy { it.id }.take(1)
                                    val nextLinks = majorPath[i].links.filter { it.entangled && !it.swappedAt(majorPath[i]) && it.contains(majorPath[i+1]) && !it.utilized}.sortedBy { it.id }.take(1)

                                    prevLinks.zip(nextLinks).forEach { (l1, l2) ->
                                        majorPath[i].attemptSwapping(l1, l2)
                                        if(majorPath[i-1]==majorPath.first()){
                                            l1.utilize()
                                        }
                                        if(majorPath[i+1]==majorPath.last()){
                                            l2.utilize()
                                        }
                                    }
                                }
                            }
                        }
                        else{
                            if(majorPath.size-i-1<=topo.k){
                                var flag=true
                                ((i-topo.k)..(majorPath.size-2)).forEach { j->
                                    if(available[j]==0) {
                                        flag=false
                                    }
                                }
                                if(flag){
                                    val prevLinks = majorPath[i].links.filter { it.entangled && !it.swappedAt(majorPath[i]) && it.contains(majorPath[i-1]) && !it.utilized}.sortedBy { it.id }.take(1)
                                    val nextLinks = majorPath[i].links.filter { it.entangled && !it.swappedAt(majorPath[i]) && it.contains(majorPath[i+1]) && !it.utilized}.sortedBy { it.id }.take(1)

                                    prevLinks.zip(nextLinks).forEach { (l1, l2) ->
                                        majorPath[i].attemptSwapping(l1, l2)
                                        if(majorPath[i-1]==majorPath.first()){
                                            l1.utilize()
                                        }
                                        if(majorPath[i+1]==majorPath.last()){
                                            l2.utilize()
                                        }
                                    }
                                }
                            }
                            else{
                                var flag=true
                                ((i-topo.k)..(i+topo.k-1)).forEach { j->
                                    if(available[j]==0) {
                                        flag=false
                                    }
                                }
                                if(flag){
                                    val prevLinks = majorPath[i].links.filter { it.entangled && !it.swappedAt(majorPath[i]) && it.contains(majorPath[i-1]) && !it.utilized}.sortedBy { it.id }.take(1)
                                    val nextLinks = majorPath[i].links.filter { it.entangled && !it.swappedAt(majorPath[i]) && it.contains(majorPath[i+1]) && !it.utilized}.sortedBy { it.id }.take(1)

                                    prevLinks.zip(nextLinks).forEach { (l1, l2) ->
                                        majorPath[i].attemptSwapping(l1, l2)
                                        if(majorPath[i-1]==majorPath.first()){
                                            l1.utilize()
                                        }
                                        if(majorPath[i+1]==majorPath.last()){
                                            l2.utilize()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
            var succ =0
            if(majorPath.size>2)
            {
                succ = topo.getEstablishedEntanglements(majorPath.first(), majorPath.last()).size - oldNumOfPairs
            }
            else {
                val SDlinks=majorPath.first().links.filter { it.entangled && it.contains(majorPath.last()) && !it.utilized }.sortedBy { it.id }
                if(SDlinks.isNotEmpty()){
                    succ= SDlinks.size.coerceAtMost(width)
                    (0..succ-1).forEach{pid->
                        SDlinks[pid].utilize()
                    }
                }
            }
            logWriter.appendln(""" ${majorPath.map { it.id }}, $width $succ""")
        }

        logWriter.appendln()
    }

     */
}